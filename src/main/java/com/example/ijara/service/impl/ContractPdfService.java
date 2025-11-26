package com.example.ijara.service.impl;

import com.example.ijara.entity.Contract;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.borders.Border;
import com.itextpdf.layout.element.*;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.VerticalAlignment;
import org.springframework.stereotype.Service;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Service
public class ContractPdfService {

    private static final String FONT_PATH = "fonts/DejaVuSans.ttf"; // Agar kerak bo'lsa, o'zbekcha harflar uchun
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd.MM.yyyy");

    public ByteArrayInputStream generateContractPdf(Contract contract) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        try (PdfWriter writer = new PdfWriter(out);
             PdfDocument pdf = new PdfDocument(writer);
             Document document = new Document(pdf, PageSize.A4)) {

            document.setMargins(50, 50, 50, 50);

            PdfFont bold = PdfFontFactory.createFont("Helvetica-Bold");
            PdfFont regular = PdfFontFactory.createFont("Helvetica");
            PdfFont italic = PdfFontFactory.createFont("Helvetica-Oblique");

            // Sarlavha
            document.add(new Paragraph("IJARA SHARTNOMASI")
                    .setFont(bold)
                    .setFontSize(20)
                    .setTextAlignment(TextAlignment.CENTER)
                    .setMarginBottom(10));

            document.add(new Paragraph("____________________________________________________________")
                    .setFontSize(12)
                    .setTextAlignment(TextAlignment.CENTER)
                    .setMarginBottom(20));

            // Sana va joy
            document.add(new Paragraph("Tuzilgan joyi: Toshkent shahri")
                    .setFont(regular)
                    .setFontSize(12));
            document.add(new Paragraph("Sana: " + LocalDate.now().format(DATE_FORMAT))
                    .setFont(regular)
                    .setFontSize(12)
                    .setMarginBottom(20));

            // Tomonlar
            Table partiesTable = new Table(2).setWidth(100);
            partiesTable.addCell(createCell("Egasi:", bold));
            partiesTable.addCell(createCell(contract.getOwner().getFirstName() + " " + contract.getOwner().getLastName(), regular));
            partiesTable.addCell(createCell("Ijara oluvchi:", bold));
            partiesTable.addCell(createCell(contract.getLessee().getFirstName() + " " + contract.getLessee().getLastName(), regular));
            document.add(partiesTable.setMarginBottom(20));

            // Shartnoma tafsilotlari
            Table detailsTable = new Table(2).setWidth(100);
            detailsTable.setFontSize(11);
            detailsTable.addCell(createCell("Mahsulot:", bold));
            detailsTable.addCell(createCell(contract.getProduct().getName(), regular));
            detailsTable.addCell(createCell("Holati:", bold));
            detailsTable.addCell(createCell(String.valueOf(contract.getContractStatus()), regular));
            detailsTable.addCell(createCell("Boshlanish sanasi:", bold));
            detailsTable.addCell(createCell(contract.getStartDateTime().format(DATE_FORMAT), regular));
            detailsTable.addCell(createCell("Tugash sanasi:", bold));
            detailsTable.addCell(createCell(contract.getEndDateTime().format(DATE_FORMAT), regular));
            detailsTable.addCell(createCell("Ijara narxi:", bold));
            detailsTable.addCell(createCell(contract.getPrice() + " so‘m", regular));
            document.add(detailsTable.setMarginBottom(25));

            // Qo'shimcha shartlar
            document.add(new Paragraph("Shartnoma amalda bo‘lgan davrda tomonlar belgilangan tartib-qoidalarga rioya qilishlari shart.")
                    .setFont(regular)
                    .setFontSize(11)
                    .setMarginBottom(40));

            // Imzo joylari
            Table signatureTable = new Table(2).setWidth(100);
            signatureTable.setMarginTop(30);
            signatureTable.addCell(createSignatureCell("Egasi:\n\n\n________________________\n(F.I.O va imzo)"));
            signatureTable.addCell(createSignatureCell("Ijara oluvchi:\n\n\n________________________\n(F.I.O va imzo)"));
            document.add(signatureTable);

            // Izoh
            document.add(new Paragraph("\nShartnoma ikki nusxada tuzildi va ikkala nusxa ham teng huquqiy kuchga ega.")
                    .setFont(italic)
                    .setFontSize(10)
                    .setTextAlignment(TextAlignment.CENTER));

        } catch (IOException e) {
            throw new RuntimeException("PDF yaratishda xatolik yuz berdi: " + e.getMessage(), e);
        }

        return new ByteArrayInputStream(out.toByteArray());
    }

    private Cell createCell(String text, PdfFont font) {
        return new Cell()
                .add(new Paragraph(text).setFont(font).setFontSize(11))
                .setBorder(Border.NO_BORDER)
                .setPadding(5);
    }

    private Cell createSignatureCell(String text) throws IOException {
        return new Cell()
                .add(new Paragraph(text)
                        .setFont(PdfFontFactory.createFont("Helvetica"))
                        .setFontSize(11)
                        .setHeight(80)
                        .setVerticalAlignment(VerticalAlignment.BOTTOM))
                .setBorder(Border.NO_BORDER)
                .setTextAlignment(TextAlignment.CENTER);
    }
}