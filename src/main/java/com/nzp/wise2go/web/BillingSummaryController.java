package com.nzp.wise2go.web;

import java.io.File;
import java.io.FileNotFoundException;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.quartz.JobBuilder;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SimpleScheduleBuilder;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import com.nzp.wise2go.entities.Customer;
import com.nzp.wise2go.entities.BillingSummary;

import com.nzp.wise2go.entities.BillingDetail;
import com.nzp.wise2go.entities.Receipt;
import com.nzp.wise2go.entities.ScheduleEmail;
import com.nzp.wise2go.exception.ResourceNotFoundException;
import com.nzp.wise2go.job.EmailJob;
import com.nzp.wise2go.repositories.CustomerRepository;
import com.nzp.wise2go.repositories.PaymentDescriptionRepository;
import com.nzp.wise2go.repositories.ReceiptRepository;
import com.nzp.wise2go.service.ReportService;

import net.sf.jasperreports.engine.JRException;

import com.nzp.wise2go.repositories.BillingDetailRepository;
import com.nzp.wise2go.repositories.BillingSummaryRepository;


@Controller
@RequestMapping("/billingsummaries")
public class BillingSummaryController
{

	@Autowired
	private CustomerRepository customerRepository;
		
	@Autowired
	private PaymentDescriptionRepository paymentDescriptionRepository;
	
	@Autowired
	private BillingSummaryRepository billingSummaryRepository;
	
	@Autowired
	private ReceiptRepository receiptRepository;
	
	@Autowired
	private BillingDetailRepository billingDetailRepository;
	
	@Autowired
	private ReportService reportService;

    
    @Autowired
    private Scheduler scheduler;

	@GetMapping("/{customerId}/list")
	public String showBillingSummaries(@PathVariable Long customerId, HttpServletRequest request, Model model) {
		Optional<Customer> customer = customerRepository.findById(customerId);
		Customer theCustomer =  customer.orElse(null);
		BillingSummary billingSummary = new BillingSummary(theCustomer);

		Boolean isPaid = false;
        int page = 0; 
        int size = 10; 
        
        if (request.getParameter("page") != null && !request.getParameter("page").isEmpty()) {
            page = Integer.parseInt(request.getParameter("page")) - 1;
        }

        if (request.getParameter("size") != null && !request.getParameter("size").isEmpty()) {
            size = Integer.parseInt(request.getParameter("size"));
        }
        
        if (request.getParameter("p") != null && !request.getParameter("p").isEmpty()) {
        	isPaid = Boolean.parseBoolean(request.getParameter("p"));
        }
		
        int available = billingSummaryRepository.findByCustomerAndIsPaidOrderByIdDesc(theCustomer, false).size();
        model.addAttribute("available", available);
		model.addAttribute("customer", theCustomer);
		model.addAttribute("billingSummary", billingSummary);
		model.addAttribute("p", isPaid);
		model.addAttribute("billingSummaries", billingSummaryRepository.findByCustomerAndIsPaidOrderByIdDesc(theCustomer, isPaid, PageRequest.of(page, size)) );

		return "billingsummary/customer-billings";
		
	}

	@GetMapping("/{customerId}/create-billing")
	public String createBillingSummary(@PathVariable Long customerId) {
		Customer theCustomer = customerRepository.findById(customerId).orElseThrow(
				() -> new ResourceNotFoundException("customer", "id", customerId));
		BillingSummary billingSummary = new BillingSummary();
		billingSummary.setCustomer(theCustomer);
		billingSummary.setMbps(theCustomer.getPlanAvail().getMaxMbps());
		billingSummary = billingSummaryRepository.save(billingSummary);
		return "redirect:/billingsummaries/"+theCustomer.getId()+"/showFormForAdd/"+billingSummary.getId();
		
	}
	
	@GetMapping("/{customerId}/showFormForAdd/{billingSummaryId}")
	public String showFormForAdd(@PathVariable Long customerId,	@PathVariable Long billingSummaryId,	
			Model theModel) {
		BillingSummary theBillingSummary = billingSummaryRepository.findById(billingSummaryId).orElseThrow(
				()-> new ResourceNotFoundException("BillingSummary", "id", billingSummaryId));
		theBillingSummary.setMbps(theBillingSummary.getCustomer().getPlanAvail().getMaxMbps());
		if(theBillingSummary.getIsPaid()) {
			return "redirect:/billingsummaries/"+theBillingSummary.getCustomer().getId()+"/list?p=true";
		}
		
		BillingDetail billingDetail = new BillingDetail();
		billingDetail.setBillingSummary(theBillingSummary);
		
		List<BillingDetail> billingDetails = billingDetailRepository.findByBillingSummaryOrderByIdDesc(theBillingSummary);
		Double totalAmmount = 0.0;
		for(BillingDetail tempBillingDetail : billingDetails) {
			totalAmmount += tempBillingDetail.getAmount();
		}
		theBillingSummary.setTotalAmount(totalAmmount);
		
		theModel.addAttribute("customer", theBillingSummary.getCustomer());
		theModel.addAttribute("paymentDescriptions", paymentDescriptionRepository.findByEnable(true));	
		theModel.addAttribute("billingSummary", theBillingSummary);
		theModel.addAttribute("billingDetail", billingDetail);
		theModel.addAttribute("billingDetails", billingDetails);
		
		return "billingsummary/customer-billing-form";
	}
	

	
	@PostMapping("/save")
	public String savePayment(@Valid @ModelAttribute("billingSummary") BillingSummary theBillingSummary, BindingResult bindingResult, Model theModel) throws Exception {
		
		String success = "created";

		if(theBillingSummary.getIsPaid()) {
			return "redirect:/billingsummaries/"+theBillingSummary.getCustomer().getId()+"/list?p=true";
		}
		
		if(bindingResult.hasErrors()) {	
			
			theModel.addAttribute("customer", theBillingSummary.getCustomer());
			
			theModel.addAttribute("paymentDescriptions", paymentDescriptionRepository.findByEnable(true));
			
	
			return "billingsummary/customer-billing-form";
		}
		BillingSummary billingSummary = billingSummaryRepository.findFirstByIsNextDueDate(true);
		if(billingSummary != null) {
			billingSummary.setIsNextDueDate(false);
			
			billingSummaryRepository.save(billingSummary);
			if(billingSummary.getCustomer().getActive() 
					&& !billingSummary.getCustomer().getEmail().isEmpty() ) {	
				scheduleEmail(billingSummary) ;
			}
				
		}
		theBillingSummary.setMbps(theBillingSummary.getCustomer().getPlanAvail().getMaxMbps());
		billingSummaryRepository.save(theBillingSummary);
		return "redirect:/billingsummaries/"+theBillingSummary.getCustomer().getId()+"/list?success="+success;
	}

	  private void scheduleEmail(BillingSummary billingSummary) throws Exception {
	    	String fileName = "\\BILLING-"+billingSummary.getCustomer().getLastName().toUpperCase()+"-"+billingSummary.getId();
	    	
	    	String path = "C:\\Users\\"+System.getProperty("user.name")+"\\Desktop\\Report\\"
	    			+ fileName+".pdf";
	    	
	    	File file = new File(path);
	    	
	    	if(!file.exists()) {
	    		 reportService.exportReportBilling("pdf", billingSummary.getCustomer().getId());
	    		 file = new File(path);
	    	}
		  	Customer customer = billingSummary.getCustomer();
			List<BillingSummary> billingSummaries = billingSummaryRepository.findByCustomerAndIsPaidOrderByIdDesc(customer,
					false);
			
			ScheduleEmail scheduleEmail = new ScheduleEmail();
			scheduleEmail.setEmail(customer.getEmail());
			scheduleEmail.setSubject("Wisetogo Bill No. "+billingSummary.getId());
			if(billingSummaries.size() == 1) {
				scheduleEmail.setBody("Dear valued customer,<br><br>Attached is your Wisetoge Billing. <br><br>If you already paid, please ignore this reminder.<br><br>Have a wonderful day!");
			}else {
				scheduleEmail.setBody("Dear valued customer,<br><br>Attached is your Wisetoge Billing. <br>Please pay immediately. <br><br>If you already paid, please ignore this reminder.<br><br>Have a wonderful day!");
			}
			LocalDate before5DaysDate = billingSummary.getNextDueDate().minusDays(5);
			scheduleEmail.setDateTime(before5DaysDate.atStartOfDay());
			scheduleEmail.setTimeZone(ZoneId.systemDefault());
			scheduleEmail.setFile(file);
	        try {

	            ZonedDateTime dateTime = ZonedDateTime.of(scheduleEmail.getDateTime(), scheduleEmail.getTimeZone());

	            JobDetail jobDetail = buildJobDetail(scheduleEmail);
	            Trigger trigger = buildJobTrigger(jobDetail, dateTime);
	            scheduler.scheduleJob(jobDetail, trigger);


	           
	        } catch (SchedulerException ex) {
	          //"Error scheduling email. Please try later!"); 
	        }
	        
	        
	    }
	  
	

	    private JobDetail buildJobDetail(ScheduleEmail scheduleEmail) {
	        JobDataMap jobDataMap = new JobDataMap();

	        jobDataMap.put("email", scheduleEmail.getEmail());
	        jobDataMap.put("subject", scheduleEmail.getSubject());
	        jobDataMap.put("body", scheduleEmail.getBody());
	        jobDataMap.put("file", scheduleEmail.getFile());

	        return JobBuilder.newJob(EmailJob.class)
	                .withIdentity(UUID.randomUUID().toString(), "email-jobs")
	                .withDescription("Send Email Job")
	                .usingJobData(jobDataMap)
	                .storeDurably()	
	                .build();
	    }

	    private Trigger buildJobTrigger(JobDetail jobDetail, ZonedDateTime startAt) {
	        return TriggerBuilder.newTrigger()
	                .forJob(jobDetail)
	                .withIdentity(jobDetail.getKey().getName(), "email-triggers")
	                .withDescription("Send Email Trigger")
	                .startAt(Date.from(startAt.toInstant()))
	                .withSchedule(SimpleScheduleBuilder.simpleSchedule().withMisfireHandlingInstructionFireNow())
	                .build();
	    }
	    
		
	@GetMapping("/{customerId}/settle-payment")
	public String showBillingSummaries(@PathVariable Long customerId,  Model model) {
		Customer theCustomer = customerRepository.findById(customerId).orElseThrow(() -> new ResourceNotFoundException("Customer", "id", customerId));
		
		List<BillingSummary> billingSummaries =  billingSummaryRepository.findByCustomerAndIsPaidOrderByIdDesc(theCustomer, false);
		
		Double totalAmount = 0.0;
		for(int index = 0; index < billingSummaries.size(); index++)
			totalAmount += billingSummaries.get(index).getTotalAmount();
		
		Receipt receipt = new Receipt();
		receipt.setDatePaid(LocalDate.now());
		receipt.setTotalAmount(totalAmount);
		receipt.setBillingSummaries(billingSummaries);		
		receipt.setCustomer(theCustomer);
		
		Receipt createdReceipt = receiptRepository.save(receipt);
		
		for(BillingSummary billingSummary : billingSummaries) {
			billingSummary.setIsPaid(true);
			billingSummaryRepository.save(billingSummary);
		}

		return "redirect:/billingsummaries/"+theCustomer.getId()+"/receipt/"+createdReceipt.getId()+"/success";
	}
	
	@GetMapping("/{customerId}/receipt/{receiptId}/success")
	public String showSuccess(@PathVariable Long customerId, @PathVariable Long receiptId, Model model) {
	
		Customer theCustomer = customerRepository.findById(customerId).orElseThrow(() -> new ResourceNotFoundException("Customer", "id", customerId));
		
		Receipt theReceipt = receiptRepository.findById(receiptId).orElseThrow(() -> new ResourceNotFoundException("Receipt", "id", receiptId));
		
		model.addAttribute("customer", theCustomer);
		
		model.addAttribute("receipt", theReceipt);
		return "redirect:/billingsummaries/"+theCustomer.getId()+"/list?success=settled account";
	}
	

	@GetMapping("/report/{customerId}/{format}")
	public String generateReport(@PathVariable String format, @PathVariable Long customerId) 
	    		throws FileNotFoundException, JRException {
	     reportService.exportReportBilling(format, customerId);
	     return "redirect:/billingsummaries/"+customerId+"/list";
	}
	

	
		
	
	


}
