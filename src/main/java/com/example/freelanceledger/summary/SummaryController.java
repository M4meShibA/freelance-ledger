package com.example.freelanceledger.summary;

import com.example.freelanceledger.user.AppUserDetails;
import com.example.freelanceledger.user.User;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.IOException;
import java.time.Year;

@Controller
public class SummaryController {

    private final SummaryService summaryService;
    private final TaxEstimationService taxEstimationService;
    private final PdfReportService pdfReportService;

    public SummaryController(SummaryService summaryService, TaxEstimationService taxEstimationService,
                              PdfReportService pdfReportService) {
        this.summaryService = summaryService;
        this.taxEstimationService = taxEstimationService;
        this.pdfReportService = pdfReportService;
    }

    @GetMapping("/dashboard")
    public String dashboard(@AuthenticationPrincipal AppUserDetails principal,
                             @RequestParam(required = false) Integer year,
                             Model model) {
        User user = principal.getUser();
        int targetYear = (year != null) ? year : Year.now().getValue();

        YearlySummary yearlySummary = summaryService.getYearlySummary(user, targetYear);
        TaxEstimate taxEstimate = taxEstimationService.estimate(
                yearlySummary.getTotalIncome(), yearlySummary.getTotalExpense());

        // グラフ描画用に、月ごとの金額のうち最大値を求めておく(棒の高さをパーセントに換算するため)
        java.math.BigDecimal maxAmount = yearlySummary.getMonths().stream()
                .map(m -> m.getTotalIncome().max(m.getTotalExpense()))
                .reduce(java.math.BigDecimal.ZERO, java.math.BigDecimal::max);
        if (maxAmount.signum() == 0) {
            maxAmount = java.math.BigDecimal.ONE; // ゼロ除算を避ける
        }

        model.addAttribute("year", targetYear);
        model.addAttribute("summary", yearlySummary);
        model.addAttribute("taxEstimate", taxEstimate);
        model.addAttribute("maxAmount", maxAmount);
        return "summary/dashboard";
    }

    @GetMapping("/dashboard/report.pdf")
    @ResponseBody
    public ResponseEntity<byte[]> downloadReport(@AuthenticationPrincipal AppUserDetails principal,
                                                  @RequestParam(required = false) Integer year) throws IOException {
        User user = principal.getUser();
        int targetYear = (year != null) ? year : Year.now().getValue();

        YearlySummary yearlySummary = summaryService.getYearlySummary(user, targetYear);
        TaxEstimate taxEstimate = taxEstimationService.estimate(
                yearlySummary.getTotalIncome(), yearlySummary.getTotalExpense());

        byte[] pdf = pdfReportService.generateAnnualReport(targetYear, yearlySummary, taxEstimate);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDispositionFormData("attachment", targetYear + "_annual_report.pdf");

        return new ResponseEntity<>(pdf, headers, org.springframework.http.HttpStatus.OK);
    }
}
