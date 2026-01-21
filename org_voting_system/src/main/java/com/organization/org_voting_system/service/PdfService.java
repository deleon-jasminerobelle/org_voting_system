package com.organization.org_voting_system.service;

import java.io.ByteArrayOutputStream;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.itextpdf.io.font.constants.StandardFonts;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;
import com.organization.org_voting_system.entity.Candidate;
import com.organization.org_voting_system.entity.Election;

@Service
public class PdfService {

    public byte[] generateElectionReportPdf(List<Election> elections,
                                          Map<Long, List<Candidate>> electionCandidates,
                                          Map<Long, Long> candidateVoteCounts) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        try {
            PdfWriter writer = new PdfWriter(outputStream);
            PdfDocument pdfDoc = new PdfDocument(writer);
            Document document = new Document(pdfDoc);

            // Create bold font
            PdfFont boldFont = PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD);

            // Title
            Paragraph title = new Paragraph("Election Results Report")
                    .setFontSize(20)
                    .setTextAlignment(TextAlignment.CENTER);
            document.add(title);
            document.add(new Paragraph("\n"));

            for (Election election : elections) {
                // Election Header
                Paragraph electionTitle = new Paragraph("Election: " + election.getTitle())
                        .setFontSize(16)
                        .setTextAlignment(TextAlignment.LEFT);
                document.add(electionTitle);

                String startDate = election.getStartDatetime() != null ? election.getStartDatetime().toLocalDate().toString() : "N/A";
                String endDate = election.getEndDatetime() != null ? election.getEndDatetime().toLocalDate().toString() : "N/A";
                Paragraph electionDetails = new Paragraph(
                        "Organization: " + election.getOrganization() + "\n" +
                        "Status: " + election.getStatus() + "\n" +
                        "Period: " + startDate + " to " + endDate
                ).setFontSize(10);
                document.add(electionDetails);
                document.add(new Paragraph("\n"));

                // Results Table
                Table table = new Table(UnitValue.createPercentArray(new float[]{4, 3, 3}));
                table.setWidth(UnitValue.createPercentValue(100));

                // Header
                table.addHeaderCell(new Cell().add(new Paragraph("Position").setFont(boldFont)));
                table.addHeaderCell(new Cell().add(new Paragraph("Candidate").setFont(boldFont)));
                table.addHeaderCell(new Cell().add(new Paragraph("Votes").setFont(boldFont)));

                List<Candidate> candidates = electionCandidates.get(election.getElectionId());
                if (candidates != null) {
                    for (Candidate candidate : candidates) {
                        table.addCell(new Cell().add(new Paragraph(candidate.getPosition().getPositionName())));
                        table.addCell(new Cell().add(new Paragraph(candidate.getFullName())));
                        Long votes = candidateVoteCounts.getOrDefault(candidate.getCandidateId(), 0L);
                        table.addCell(new Cell().add(new Paragraph(votes.toString())));
                    }
                }

                document.add(table);
                document.add(new Paragraph("\n\n"));
            }

            document.close();
        } catch (Exception e) {
            throw new RuntimeException("Error generating PDF report", e);
        }

        return outputStream.toByteArray();
    }
}
