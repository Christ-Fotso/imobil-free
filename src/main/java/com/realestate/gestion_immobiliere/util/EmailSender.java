package com.realestate.gestion_immobiliere.util;

import java.util.Properties;
import javax.mail.*;
import javax.mail.internet.*;
import java.util.Random;

public class EmailSender {

    // Méthode pour envoyer un email avec un code de vérification à 4 chiffres
    public static int sendEmail(String to) {
        // Générer un code de vérification à 4 chiffres
        Random random = new Random();
        int verificationCode = 1000 + random.nextInt(9000); // Génère un nombre entre 1000 et 9999

        // Configuration des propriétés SMTP
        String host = "smtp.gmail.com";  // Utilisation de Gmail pour l'exemple
        final String username = "djechefotsochristarole@gmail.com";  // Votre adresse email
        final String password = "uzhmgpfjgvdcomsq";  // Votre mot de passe ou mot de passe d'application

        // Paramètres du serveur
        Properties properties = System.getProperties();
        properties.setProperty("mail.smtp.host", host);
        properties.setProperty("mail.smtp.port", "587");  // Port SMTP pour Gmail
        properties.setProperty("mail.smtp.auth", "true");
        properties.setProperty("mail.smtp.starttls.enable", "true");  // Sécuriser la connexion

        // Créez une session avec les informations d'identification de l'utilisateur
        Session session = Session.getInstance(properties, new javax.mail.Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(username, password);
            }
        });

        try {
            // Créer un message email
            MimeMessage message = new MimeMessage(session);
            message.setFrom(new InternetAddress(username));
            message.addRecipient(Message.RecipientType.TO, new InternetAddress(to));
            message.setSubject("Code de Vérification 2FA");
            message.setText("Votre code de vérification à 2 facteurs est : " + verificationCode);

            // Envoi de l'email
            Transport.send(message);
            System.out.println("Email envoyé avec succès.");
        } catch (MessagingException e) {
            e.printStackTrace();
        }

        // Retourner le code généré pour être utilisé dans la logique 2FA
        return verificationCode;
    }

    // Méthode principale pour tester l'envoi d'un email avec le code
    public static void main(String[] args) {
        int code = sendEmail("djechefarole@gmail.com");
        System.out.println("Le code généré est : " + code);
    }
}
