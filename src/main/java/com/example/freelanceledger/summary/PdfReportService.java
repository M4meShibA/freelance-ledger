package com.example.freelanceledger.summary;

import com.lowagie.text.*;
import com.lowagie.text.pdf.BaseFont;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;

/**
 * 年間の収支・税金概算を簡易PDFレポートとして出力するサービス。
 *
 * 注意: これは「青色申告決算書」そのものの様式を再現したものではなく、
 * ポートフォリオ用に作った簡易版の年間レポート。正式な確定申告には
 * 国税庁の様式や会計ソフトの出力を使うこと。
 *
 * フォントについて: 日本語表示のため、CIDフォント「HeiseiKakuGo-W5」を
 * 埋め込みなしで使用している。多くのPDFビューアでは表示できるが、
 * 環境によっては日本語フォント代替パックが必要になる場合がある。
 * 本番運用するなら、埋め込み可能な日本語TTFフォント(IPAフォント等)に
 * 差し替えるのが望ましい。
 */
@Service
public class PdfReportService {

    public byte[] generateAnnualReport(int year, YearlySummary summary, TaxEstimate taxEstimate) throws IOException {
        Document document = new Document(PageSize.A4);
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        try {
            PdfWriter.getInstance(document, out);
            document.open();

            BaseFont baseFont = BaseFont.createFont("HeiseiKakuGo-W5", "UniJIS-UCS2-H", BaseFont.NOT_EMBEDDED);
            Font titleFont = new Font(baseFont, 16, Font.BOLD);
            Font headerFont = new Font(baseFont, 11, Font.BOLD);
            Font bodyFont = new Font(baseFont, 10, Font.NORMAL);
            Font noteFont = new Font(baseFont, 8, Font.NORMAL);

            Paragraph title = new Paragraph(year + "年 収支レポート(簡易版)", titleFont);
            title.setSpacingAfter(16);
            document.add(title);

            // --- 年間サマリー ---
            PdfPTable summaryTable = new PdfPTable(2);
            summaryTable.setWidthPercentage(100);
            addRow(summaryTable, "年間収入合計", formatYen(summary.getTotalIncome()), headerFont, bodyFont);
            addRow(summaryTable, "年間経費合計", formatYen(summary.getTotalExpense()), headerFont, bodyFont);
            addRow(summaryTable, "事業所得(収入-経費)", formatYen(summary.getTotalProfit()), headerFont, bodyFont);
            summaryTable.setSpacingAfter(16);
            document.add(summaryTable);

            // --- 月別内訳 ---
            Paragraph monthlyTitle = new Paragraph("月別内訳", headerFont);
            monthlyTitle.setSpacingAfter(8);
            document.add(monthlyTitle);

            PdfPTable monthlyTable = new PdfPTable(4);
            monthlyTable.setWidthPercentage(100);
            addHeaderCell(monthlyTable, "月", headerFont);
            addHeaderCell(monthlyTable, "収入", headerFont);
            addHeaderCell(monthlyTable, "経費", headerFont);
            addHeaderCell(monthlyTable, "差引", headerFont);

            for (MonthlySummary m : summary.getMonths()) {
                addBodyCell(monthlyTable, m.getMonth().getMonthValue() + "月", bodyFont);
                addBodyCell(monthlyTable, formatYen(m.getTotalIncome()), bodyFont);
                addBodyCell(monthlyTable, formatYen(m.getTotalExpense()), bodyFont);
                addBodyCell(monthlyTable, formatYen(m.getProfit()), bodyFont);
            }
            monthlyTable.setSpacingAfter(16);
            document.add(monthlyTable);

            // --- 税金概算 ---
            Paragraph taxTitle = new Paragraph("税金・社会保険料の概算", headerFont);
            taxTitle.setSpacingAfter(8);
            document.add(taxTitle);

            PdfPTable taxTable = new PdfPTable(2);
            taxTable.setWidthPercentage(100);
            addRow(taxTable, "所得税(概算)", formatYen(taxEstimate.getIncomeTax()), headerFont, bodyFont);
            addRow(taxTable, "住民税(概算)", formatYen(taxEstimate.getResidentTax()), headerFont, bodyFont);
            addRow(taxTable, "国民健康保険料(概算)", formatYen(taxEstimate.getNationalHealthInsurance()), headerFont, bodyFont);
            addRow(taxTable, "税・保険料 合計(概算)", formatYen(taxEstimate.getTotalTaxAndInsurance()), headerFont, bodyFont);
            addRow(taxTable, "概算の手取り", formatYen(taxEstimate.getEstimatedNetProfit()), headerFont, bodyFont);
            taxTable.setSpacingAfter(16);
            document.add(taxTable);

            Paragraph note = new Paragraph(
                    "※ 本レポートは学習・ポートフォリオ用の簡易試算です。実際の税額・控除額は" +
                    "個人の状況(扶養控除、社会保険料控除、自治体の保険料率など)により異なります。" +
                    "正確な金額は税理士または確定申告ソフトでご確認ください。", noteFont);
            document.add(note);

        } catch (DocumentException e) {
            throw new IOException("PDF生成中にエラーが発生しました", e);
        } finally {
            document.close();
        }

        return out.toByteArray();
    }

    private void addRow(PdfPTable table, String label, String value, Font labelFont, Font valueFont) {
        table.addCell(new PdfPCell(new Phrase(label, labelFont)));
        PdfPCell valueCell = new PdfPCell(new Phrase(value, valueFont));
        valueCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
        table.addCell(valueCell);
    }

    private void addHeaderCell(PdfPTable table, String text, Font font) {
        PdfPCell cell = new PdfPCell(new Phrase(text, font));
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        table.addCell(cell);
    }

    private void addBodyCell(PdfPTable table, String text, Font font) {
        PdfPCell cell = new PdfPCell(new Phrase(text, font));
        cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
        table.addCell(cell);
    }

    private String formatYen(BigDecimal amount) {
        return String.format("%,d 円", amount.longValueExact());
    }
}
