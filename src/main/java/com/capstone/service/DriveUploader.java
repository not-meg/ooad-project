package com.capstone.service;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.FileContent;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.DriveScopes;
import com.google.api.services.drive.model.File;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.FileOutputStream;
import java.security.GeneralSecurityException;
import java.util.Collections;

public class DriveUploader {

    private static final String APPLICATION_NAME = "Capstone Drive Uploader";
    private static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();

    // ✅ Replace this with the actual absolute path to your OAuth 2.0 credentials file
    private static final String CREDENTIALS_FILE_PATH = "src/main/resources/credentials.json";

    // 🔒 Scope defines what kind of access you want — this one is "upload only"
    private static final java.util.List<String> SCOPES = Collections.singletonList(DriveScopes.DRIVE_FILE);

    /**
     * Loads credentials from credentials.json and initializes Google Drive API client
     */
    private static Drive getDriveService() throws IOException, GeneralSecurityException {
        final NetHttpTransport httpTransport = GoogleNetHttpTransport.newTrustedTransport();

        // Load client secrets
        InputStreamReader reader = new InputStreamReader(new FileInputStream(CREDENTIALS_FILE_PATH));
        GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(JSON_FACTORY, reader);

        // Build the flow and trigger user authorization
        GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(
                httpTransport, JSON_FACTORY, clientSecrets, SCOPES)
                .setAccessType("offline") // Persist access even after app closes
                .build();

        Credential credential = new AuthorizationCodeInstalledApp(
                flow,
                new LocalServerReceiver.Builder().setPort(8888).build()
        ).authorize("user");

        return new Drive.Builder(httpTransport, JSON_FACTORY, credential)
                .setApplicationName(APPLICATION_NAME)
                .build();
    }

    /**
     * Uploads the given file to Google Drive and returns the file ID.
     */
    public static String uploadFile(java.io.File fileToUpload) {
        try {
            Drive service = getDriveService();

            File fileMetadata = new File();
            fileMetadata.setName(fileToUpload.getName());

            FileContent mediaContent = new FileContent("application/octet-stream", fileToUpload);

            File uploadedFile = service.files().create(fileMetadata, mediaContent)
                    .setFields("id")
                    .execute();

            System.out.println("✅ File uploaded. ID: " + uploadedFile.getId());

            return uploadedFile.getId(); // You can store this as the Drive reference
        } catch (IOException | GeneralSecurityException e) {
            System.err.println("❌ Failed to upload file to Google Drive:");
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Retrieves a file from Google Drive by its file ID.
     * This will download the file to the specified path.
     */
    public static void retrieveFile(String fileId, String destinationPath) {
        try {
            Drive service = getDriveService();

            // Request the file's metadata
            File file = service.files().get(fileId).execute();

            // Download the file
            service.files().get(fileId)
                    .executeMediaAndDownloadTo(new FileOutputStream(destinationPath));

            System.out.println("✅ File downloaded to: " + destinationPath);
        } catch (IOException | GeneralSecurityException e) {
            System.err.println("❌ Failed to retrieve file from Google Drive:");
            e.printStackTrace();
        }
    }
}
