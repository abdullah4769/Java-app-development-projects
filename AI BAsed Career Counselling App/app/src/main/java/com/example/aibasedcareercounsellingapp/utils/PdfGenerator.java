package com.example.aibasedcareercounsellingapp.utils;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.pdf.PdfDocument;
import android.os.Environment;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class PdfGenerator {

    public static void generateResume(Context context, String name, String email, String phone, 
                                      String education, String skills, String experience, String summary) {
        
        PdfDocument doc = new PdfDocument();
        PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(595, 842, 1).create(); // A4 size
        PdfDocument.Page page = doc.startPage(pageInfo);
        
        Canvas canvas = page.getCanvas();
        Paint paint = new Paint();
        
        int x = 40;
        int y = 60;
        
        // Name (Large, Bold)
        paint.setColor(Color.BLACK);
        paint.setTextSize(26);
        paint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
        canvas.drawText(name, x, y, paint);
        
        y += 30;
        
        // Contact Info (Phone | Email)
        paint.setTextSize(12);
        paint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.NORMAL));
        paint.setColor(Color.DKGRAY);
        canvas.drawText(email + " | " + phone, x, y, paint);
        
        y += 20;
        // Header Separator
        paint.setStrokeWidth(2);
        paint.setColor(Color.BLACK);
        canvas.drawLine(x, y, 555, y, paint); 
        
        y += 40;

        // SUMMARY SECTION
        if (!summary.isEmpty()) {
            paint.setColor(Color.BLUE);
            paint.setTextSize(18);
            paint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
            canvas.drawText("PROFESSIONAL SUMMARY", x, y, paint);

            y += 25;
            paint.setColor(Color.BLACK);
            paint.setTextSize(12);
            paint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.NORMAL));
            
            // Multiline Summary
            int charPerLine = 95; 
            for (int i = 0; i < summary.length(); i += charPerLine) {
                int end = Math.min(i + charPerLine, summary.length());
                canvas.drawText(summary.substring(i, end).trim(), x, y, paint);
                y += 15;
            }
            y += 15;
            
            // Summary Separator
            paint.setStrokeWidth(1);
            paint.setColor(Color.LTGRAY);
            canvas.drawLine(x, y, 555, y, paint);
            y += 30;
        }
        
        // EDUCATION SECTION
        paint.setColor(Color.BLUE);
        paint.setTextSize(18);
        paint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
        canvas.drawText("EDUCATION", x, y, paint);
        
        y += 25;
        paint.setColor(Color.BLACK);
        paint.setTextSize(12);
        paint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.NORMAL));
        for (String line : education.split("\n")) {
            canvas.drawText("• " + line.trim(), x, y, paint);
            y += 20;
        }
        y += 10;
        // Education Separator
        paint.setStrokeWidth(1);
        paint.setColor(Color.LTGRAY);
        canvas.drawLine(x, y, 555, y, paint);
        y += 30;
        
        // SKILLS SECTION (2-Column Table)
        paint.setColor(Color.BLUE);
        paint.setTextSize(18);
        paint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
        canvas.drawText("SKILLS", x, y, paint);
        
        y += 30;
        paint.setColor(Color.BLACK);
        paint.setTextSize(12);
        paint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.NORMAL));
        
        String[] skillList = skills.contains(",") ? skills.split(",") : skills.split("\n");
        int col1X = x;
        int col2X = x + 250; // Second column starts at X+250
        
        for (int i = 0; i < skillList.length; i++) {
            String skill = "• " + skillList[i].trim();
            if (i % 2 == 0) {
                canvas.drawText(skill, col1X, y, paint);
            } else {
                canvas.drawText(skill, col2X, y, paint);
                y += 20; // Move to next row after 2nd column
            }
        }
        // If odd number of skills, move y down for the last incomplete row
        if (skillList.length % 2 != 0) {
            y += 20;
        }
        
        y += 10;
        // Skills Separator
        paint.setStrokeWidth(1);
        paint.setColor(Color.LTGRAY);
        canvas.drawLine(x, y, 555, y, paint);
        y += 30;

        // EXPERIENCE SECTION
        paint.setColor(Color.BLUE);
        paint.setTextSize(18);
        paint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
        canvas.drawText("WORK EXPERIENCE", x, y, paint);
        
        y += 25;
        paint.setColor(Color.BLACK);
        paint.setTextSize(12);
        paint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.NORMAL));
        for (String line : experience.split("\n")) {
            canvas.drawText(line.trim(), x, y, paint);
            y += 20;
        }
        
        doc.finishPage(page);
        
        // Write to file
        String fileName = "Resume_" + name.replaceAll("\\s+", "_") + "_" + System.currentTimeMillis() + ".pdf";
        File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), fileName);
        
        try {
            doc.writeTo(new FileOutputStream(file));
            Toast.makeText(context, "PDF Saved: " + fileName, Toast.LENGTH_LONG).show();
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(context, "Error saving PDF: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
        
        doc.close();
    }
}
