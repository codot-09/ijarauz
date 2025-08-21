package com.example.ijara.service.impl;

import com.example.ijara.entity.Contract;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.text.pdf.draw.LineSeparator;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.time.LocalDate;

@Service
public class ContractPdfService {

    public ByteArrayInputStream generateContractPdf(Contract contract) {
        Document document = new Document(PageSize.A4, 50, 50, 50, 50);
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        try {
            PdfWriter.getInstance(document, out);
            document.open();

            // Kompaniya logotipi (agar bo‘lsa)
            // Image logo = Image.getInstance("src/main/resources/logo.png");
            // logo.scaleAbsolute(80, 80);
            // logo.setAlignment(Element.ALIGN_RIGHT);
            // document.add(logo);

            // Title
//            Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 20);
//            Paragraph title = new Paragraph("IJARA SHARTNOMASI", titleFont);
//            title.setAlignment(Element.ALIGN_CENTER);
//            document.add(title);
//
//            document.add(new Paragraph(" "));
//            document.add(new Paragraph(" "));
//
//            // Bandlar ko‘rinishida
//            Font bold = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12);
//            Font normal = FontFactory.getFont(FontFactory.HELVETICA, 12);
//
//            document.add(new Paragraph("1. Tomonlar", bold));
//            document.add(new Paragraph("   Egasi: " + contract.getOwner().getFirstName() + " " + contract.getOwner().getLastName(), normal));
//            document.add(new Paragraph("   Ijara oluvchi: " + contract.getLessee().getFirstName() + " " + contract.getLessee().getLastName(), normal));
//            document.add(new Paragraph(" "));
//
//            document.add(new Paragraph("2. Shartnoma predmeti", bold));
//            document.add(new Paragraph("   Mahsulot: " + contract.getProduct().getName(), normal));
//            document.add(new Paragraph("   Holati: " + contract.getContractStatus(), normal));
//            document.add(new Paragraph(" "));
//
//            document.add(new Paragraph("3. Muddat", bold));
//            document.add(new Paragraph("   Boshlanish: " + contract.getStartDateTime(), normal));
//            document.add(new Paragraph("   Tugash: " + contract.getEndDateTime(), normal));
//            document.add(new Paragraph(" "));
//
//            document.add(new Paragraph("4. To‘lov", bold));
//            document.add(new Paragraph("   Narxi: " + contract.getPrice() + " so‘m", normal));
//            document.add(new Paragraph(" "));
//
//            document.add(new Paragraph("5. Qo‘shimcha shartlar", bold));
//            document.add(new Paragraph("   Shartnoma amalda bo‘lgan davrda tomonlar belgilangan tartibga rioya qilishlari shart.", normal));
//            document.add(new Paragraph(" "));
//
//            document.add(new Paragraph(" "));
//
//            // Imzo joylari
//            PdfPTable table = new PdfPTable(2);
//            table.setWidthPercentage(100);
//            table.setSpacingBefore(50);
//
//            PdfPCell ownerCell = new PdfPCell(new Phrase("Egasi:\n\n\n\n_____________________", normal));
//            ownerCell.setBorder(Rectangle.NO_BORDER);
//
//            PdfPCell lesseeCell = new PdfPCell(new Phrase("Ijara oluvchi:\n\n\n\n_____________________", normal));
//            lesseeCell.setBorder(Rectangle.NO_BORDER);
//
//            table.addCell(ownerCell);
//            table.addCell(lesseeCell);
//
//            document.add(table);

            Paragraph header = new Paragraph("IJARA SHARTNOMASI",
                    FontFactory.getFont(FontFactory.HELVETICA_BOLD, 20));
            header.setAlignment(Element.ALIGN_CENTER);
            document.add(header);

            LineSeparator line = new LineSeparator();
            document.add(line);

            document.add(new Paragraph("\nTuzilgan joyi: Toshkent"));
            document.add(new Paragraph("Sana: " + LocalDate.now()));
            document.add(new Paragraph("\n"));

// Tomonlar jadvali
            PdfPTable parties = new PdfPTable(2);
            parties.setWidthPercentage(100);
            parties.addCell(new Phrase("Egasi: " + contract.getOwner().getFirstName()));
            parties.addCell(new Phrase("Ijara oluvchi: " + contract.getLessee().getFirstName()));
            document.add(parties);

// Mahsulot va muddat jadvali
            PdfPTable details = new PdfPTable(2);
            details.setWidthPercentage(100);
            details.addCell("Mahsulot: " + contract.getProduct().getName());
            details.addCell("Holati: " + contract.getContractStatus());
            details.addCell("Boshlanish: " + contract.getStartDateTime());
            details.addCell("Tugash: " + contract.getEndDateTime());
            details.addCell("Narxi: " + contract.getPrice() + " so‘m");
            document.add(details);

            document.add(new Paragraph("\nShartnoma amalda bo‘lgan davrda tomonlar belgilangan tartibga rioya qilishlari shart.\n\n"));

// Imzo joylari
            PdfPTable signatures = new PdfPTable(2);
            signatures.setWidthPercentage(100);
            signatures.addCell("Egasi:\n\n______________\n(F.I.O)");
            signatures.addCell("Ijara oluvchi:\n\n______________\n(F.I.O)");
            document.add(signatures);

            document.add(new Paragraph("\n\nShartnoma ikki nusxada tuzildi, ikkalasi ham teng huquqli.",
                    FontFactory.getFont(FontFactory.HELVETICA_OBLIQUE, 10)));
            document.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return new ByteArrayInputStream(out.toByteArray());
    }

}
