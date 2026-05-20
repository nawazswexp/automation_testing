package com.purebi.utils;

import java.io.File;
import java.util.Base64;
import java.util.Properties;
import jakarta.mail.*;
import jakarta.mail.internet.*;

public class EmailUtil {

    public static void sendReportByEmail() {
        System.out.println("\uD83D\uDD25 EmailUtil.sendReportByEmail() CALLED");

        // Prefer environment variables so credentials aren't hard-coded.
        final String fromEmail = System.getenv().getOrDefault("EMAIL_FROM", "noreply@pure.bi");
        final String password  = System.getenv().getOrDefault("EMAIL_PASSWORD", "ryzl daww vtsq zosn");
        final String toEmail   = System.getenv().getOrDefault("EMAIL_TO", "nawaz@softwareexp.com");

        Properties props = new Properties();
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.starttls.required", "true");
        props.put("mail.smtp.ssl.trust", "smtp.gmail.com");
        props.put("mail.debug", "true");

        Session session = Session.getInstance(props,
            new Authenticator() {
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(fromEmail, password);
                }
            });

        // Enable verbose SMTP debug (will print SMTP conversation to console)
        session.setDebug(true);

        // Basic validation to catch common misconfiguration early
        if (fromEmail == null || !fromEmail.contains("@")) {
            throw new RuntimeException("Invalid from email address: " + fromEmail + ". Set EMAIL_FROM env var to a valid email (e.g., your Gmail address).");
        }

        System.out.println("Using mail.from=" + fromEmail + "  mail.to=" + toEmail);

        try {
            // ✅ Prefer the standard TestNG emailable report if present
            File reportsDir = new File(System.getProperty("user.dir"), "target/surefire-reports");
            File report = new File(reportsDir, "emailable-report.html");

            // If emailable-report is not present, try to find the newest HTML file in the reports folder (recursively)
            if (!report.exists()) {
                System.out.println("emailable-report.html not found, scanning " + reportsDir.getPath() + " for any HTML report (recursive)...");
                java.nio.file.Path reportsPath = reportsDir.toPath();
                java.util.Optional<java.nio.file.Path> newest = java.nio.file.Files.walk(reportsPath)
                    .filter(p -> p.toString().toLowerCase().endsWith(".html"))
                    .filter(p -> !p.getFileName().toString().toLowerCase().startsWith("jquery") && !p.getFileName().toString().toLowerCase().startsWith("index"))
                    .max((a, b) -> Long.compare(a.toFile().lastModified(), b.toFile().lastModified()));

                if (newest.isPresent()) {
                    report = newest.get().toFile();
                    System.out.println("Found alternative report file: " + report.getAbsolutePath());
                } else {
                    // No HTML files found — log directory listing and fail fast (no long wait)
                    System.out.println("No HTML reports present in " + reportsDir.getAbsolutePath());
                    File[] allFiles = reportsDir.listFiles();
                    if (allFiles != null) {
                        System.out.println("Directory contents:");
                        for (File f : allFiles) System.out.println(" - " + f.getName());
                    }
                    throw new RuntimeException("❌ No report file found to send in " + reportsDir.getAbsolutePath());
                }
            }
            System.out.println("📄 Report selected: " + report.getAbsolutePath());

            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(fromEmail));
            message.setRecipients(
                Message.RecipientType.TO,
                InternetAddress.parse(toEmail)
            );
            message.setSubject("PUREBI Automation TestNG Report");


            // Read HTML report content
            String htmlContent = new String(java.nio.file.Files.readAllBytes(report.toPath()), java.nio.charset.StandardCharsets.UTF_8);

            // Replace known selector strings with friendly names for clearer email reports
            try {
                htmlContent = com.purebi.utils.Locators.replaceSelectorsWithNames(htmlContent);
            } catch (Exception e) {
                System.err.println("Warning: failed to replace selectors with names in report: " + e.getMessage());
            }

            // Attach screenshots as inline MIME parts and update <img> references to use cid: links
            Multipart relatedMultipart = new MimeMultipart("related");

            // We'll create the HTML part later after we update img src to cid:...
            java.util.Map<String, File> inlineImages = new java.util.HashMap<>();
            try {
                File screenshotsDir = new File(reportsDir, "screenshots");
                if (screenshotsDir.exists() && screenshotsDir.isDirectory()) {
                    File[] imgs = screenshotsDir.listFiles((d, name) -> {
                        String ln = name.toLowerCase();
                        return ln.endsWith(".png") || ln.endsWith(".jpg") || ln.endsWith(".jpeg") || ln.endsWith(".gif");
                    });
                    if (imgs != null) {
                        for (File img : imgs) {
                            String rel = "screenshots/" + img.getName();
                            if (htmlContent.contains(rel)) {
                                // use the file name as CID
                                String cid = img.getName();
                                inlineImages.put(cid, img);
                                // replace image src to use cid
                                htmlContent = htmlContent.replaceAll("src=([\"'])" + java.util.regex.Pattern.quote(rel) + "\\1", "src=\"cid:" + cid + "\"");
                            }
                        }
                    }
                }
            } catch (Exception e) {
                System.err.println("Error while preparing inline screenshots: " + e.getMessage());
            }

            // If we have inline images, build a multipart/related message
            if (!inlineImages.isEmpty()) {
                // HTML body part
                jakarta.mail.internet.MimeBodyPart htmlPart = new jakarta.mail.internet.MimeBodyPart();
                htmlPart.setContent(htmlContent, "text/html; charset=UTF-8");
                relatedMultipart.addBodyPart(htmlPart);

                // Attach each image as inline
                for (java.util.Map.Entry<String, File> e : inlineImages.entrySet()) {
                    String cid = e.getKey();
                    File img = e.getValue();
                    try {
                        jakarta.activation.DataSource fds = new jakarta.activation.FileDataSource(img);
                        jakarta.mail.internet.MimeBodyPart imagePart = new jakarta.mail.internet.MimeBodyPart();
                        imagePart.setDataHandler(new jakarta.activation.DataHandler(fds));
                        imagePart.setHeader("Content-ID", "<" + cid + ">");
                        imagePart.setDisposition(jakarta.mail.Part.INLINE);
                        relatedMultipart.addBodyPart(imagePart);
                    } catch (Exception ex) {
                        System.err.println("Failed to attach inline image: " + img.getAbsolutePath());
                        ex.printStackTrace();
                    }
                }

                message.setContent(relatedMultipart);
            } else {
                // No inline images — send plain HTML
                message.setContent(htmlContent, "text/html; charset=UTF-8");
            }

            System.out.println("📧 Sending email to: " + toEmail + " from: " + fromEmail);
            Transport.send(message);
            System.out.println("✅ EMAIL SENT SUCCESSFULLY");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
