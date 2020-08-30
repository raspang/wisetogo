package com.nzp.wise2go.web;

import java.io.FileNotFoundException;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttributes;

import com.nzp.wise2go.entities.Customer;
import com.nzp.wise2go.entities.BillingSummary;
import com.nzp.wise2go.entities.PaymentDescription;
import com.nzp.wise2go.entities.BillingDetail;
import com.nzp.wise2go.entities.Receipt;
import com.nzp.wise2go.exception.ResourceNotFoundException;
import com.nzp.wise2go.repositories.CustomerRepository;
import com.nzp.wise2go.repositories.PaymentDescriptionRepository;
import com.nzp.wise2go.repositories.ReceiptRepository;
import com.nzp.wise2go.service.ReportService;

import net.sf.jasperreports.engine.JRException;

import com.nzp.wise2go.repositories.BillingDetailRepository;
import com.nzp.wise2go.repositories.BillingSummaryRepository;


@Controller
@RequestMapping("/billingdetails")
public class BillingDetailController
{

	@Autowired
	private BillingDetailRepository billingDetailRepository;
		
	@Autowired
	private BillingSummaryRepository billingSummaryRepository;
	


	@PostMapping("/{billingSummaryId}/create-billing-detail")
	public String createBillingDetail(@PathVariable Long billingSummaryId,
			@Valid @ModelAttribute("billingDetail") BillingDetail billingDetail,
			Model model) {
		BillingSummary theBillingSummary = billingSummaryRepository.findById(billingSummaryId).orElseThrow( 
				() -> new ResourceNotFoundException("BillingSummary", "id", billingSummaryId));
		billingDetailRepository.save(billingDetail);		
		return "redirect:/billingsummaries/"+theBillingSummary.getCustomer().getId()+"/showFormForAdd/"+theBillingSummary.getId();
	}
	
	@GetMapping("/{billingSummaryId}/remove-billing-detail/{billingDetailId}")
	public String removeBillingDetail(@PathVariable Long billingSummaryId, @PathVariable Long billingDetailId, Model model) {
		BillingSummary theBillingSummary = billingSummaryRepository.findById(billingSummaryId).orElseThrow( 
				() -> new ResourceNotFoundException("BillingSummary", "id", billingSummaryId));
		billingDetailRepository.deleteById(billingDetailId);
		return "redirect:/billingsummaries/"+theBillingSummary.getCustomer().getId()+"/showFormForAdd/"+theBillingSummary.getId();
		
	}
	
}
