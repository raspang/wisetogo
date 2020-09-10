package com.nzp.wise2go.web;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import com.nzp.wise2go.entities.BillingSummary;
import com.nzp.wise2go.entities.BillingDetail;
import com.nzp.wise2go.exception.ResourceNotFoundException;
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
