package javasrc;

import java.io.*;
import java.util.*;
import javax.mail.*;
import javax.mail.Flags.Flag;
import javax.mail.search.FlagTerm;
import javax.swing.JOptionPane;


class OutlookReader
{

    private String folderName;
    private boolean debug;
    private HashMap<String, String> fileNamesPlusAttachments;
    private String userEmail;
    private String userPassword;

    OutlookReader(String emailIn, String passwordIn)
    {
        debug = false;
        fileNamesPlusAttachments = new HashMap<>();
        userEmail = emailIn;
        userPassword = passwordIn;
        folderName = "Splunk Reports";
    }

    HashMap<String, String> retrieveEmails()
    {
        fileNamesPlusAttachments = new HashMap<>();

        Properties props = System.getProperties();
        props.setProperty("mail.store.protocol", "imaps");
        props.setProperty("mail.smtp.port", "587");
        try
        {
            Session session = Session.getDefaultInstance(props, null);
            Store store = session.getStore("imaps");
            if(!store.isConnected())
            {
                store.connect("imap-mail.outlook.com", userEmail, userPassword);
            }


            Folder inbox = store.getFolder(folderName);
            System.err.println("No of Unread Messages : " + inbox.getUnreadMessageCount());


            inbox.open(Folder.READ_ONLY);


            Message[] messages = inbox.search(new FlagTerm(new Flags(Flag.SEEN), false));


            FetchProfile fp = new FetchProfile();
            fp.add(FetchProfile.Item.ENVELOPE);
            fp.add(FetchProfile.Item.CONTENT_INFO);
            inbox.fetch(messages, fp);

            try
            {
                printAllMessages(messages);
                inbox.close(true);
                store.close();
            }
            catch (Exception ex)
            {
                System.out.println("EXCEPTION");
                String stackTrace  = StackTraceUtil.buildStackTrace(ex);
                Popup.displayMessage(300, 300, stackTrace, "Exception", JOptionPane.ERROR_MESSAGE, true);
            }

        }
        catch (NoSuchProviderException e)
        {
            System.out.println("NOSUCHPROVIDEREXCEPTION EXCEPTION");
            String stackTrace = StackTraceUtil.buildStackTrace(e);
            Popup.displayMessage(300, 300, stackTrace, "Exception: No Such Provider", JOptionPane.ERROR_MESSAGE, true);
        }
        catch (AuthenticationFailedException e)
        {
            System.out.println("AUTHENTICATION FAILED EXCEPTION");
            String stackTrace = StackTraceUtil.buildStackTrace(e);
            Popup.displayMessage(300, 300, stackTrace, "Exception: Authentication Failed" , JOptionPane.ERROR_MESSAGE, true);
        }
        catch (MessagingException e)
        {
            System.out.println("MESSAGING EXCEPTION");
            String stackTrace = StackTraceUtil.buildStackTrace(e);
            Popup.displayMessage(300, 300, stackTrace, "Exception: Messaging (General)", JOptionPane.ERROR_MESSAGE, true);
        }

        return fileNamesPlusAttachments;
    }

    private void printAllMessages(Message[] msgs) throws Exception
    {
        for (Message msg : msgs) {
            Address[] fromAddress = msg.getFrom();
            if (debug && fromAddress != null) {
                System.out.println("-------------------------------------------------------");
                String nameAddr = fromAddress[0].toString();
                System.out.println("NAME: " + nameAddr);
                System.out.println("-------------------------------------------------------");
            }


            if (fromAddress != null) {
                printEnvelope(msg);
            }
        }
    }

    /*  Print the envelope(FromAddress,ReceivedDate,Subject)  */
    private void printEnvelope(Message message) throws Exception
    {
        Address[] addresses;

        if ((addresses = message.getFrom()) != null)
        {
            for (Address address : addresses) {
                if (debug) {
                    System.out.println("FROM: " + address.toString());
                }
            }
        }

        if ((addresses = message.getRecipients(Message.RecipientType.TO)) != null)
        {
            for (Address address : addresses) {
                if (debug) {
                    System.out.println("TO: " + address.toString());
                }
            }
        }
        String subject = message.getSubject();
        String[] subjectPieces = subject.split(" ");
        String reportName = subjectPieces[2];

        Date receivedDate = message.getReceivedDate();
        String content = message.getContent().toString();
        if(debug)System.out.println("Subject : " + subject);
        if(debug)System.out.println("Received Date : " + receivedDate.toString());
        if(debug)System.out.println("Content : " + content);
        getContent(reportName, message);

    }

    private void getContent(String reportName, Message msg)
    {
        try
        {
            String contentType = msg.getContentType();
            if(debug) System.out.println("Content Type : " + contentType);
            Multipart mp = (Multipart) msg.getContent();
            int count = mp.getCount();
            for (int i = 0; i < count; i++)
            {
                String attachment = getAttachment(mp.getBodyPart(i));
                fileNamesPlusAttachments.put(reportName, attachment);
            }
        }
        catch (Exception ex)
        {
            System.out.println("EXCEPTION: getContent");
            String stackTrace = StackTraceUtil.buildStackTrace(ex);
            Popup.displayMessage(300, 300, stackTrace, "Exception", JOptionPane.ERROR_MESSAGE, true);
        }
    }

    private String getAttachment(Part p) throws Exception
    {
        StringBuilder attachmentContent = new StringBuilder();
        InputStream is = p.getInputStream();

        if (!(is instanceof BufferedInputStream))
        {
            is = new BufferedInputStream(is);
        }
        int c;
        boolean reachedAttachment = false;

        while ((c = is.read()) != -1)
        {
            if(!debug) {
                if  (c == '_' && (c = is.read()) == 't' &&
                    (c = is.read()) == 'i' &&
                    (c = is.read()) == 'm' &&
                    (c = is.read()) == 'e' &&
                    (c = is.read()) == ',') {
                    attachmentContent.append("_time,");
                    reachedAttachment = true;
                    continue;
                }
                if (reachedAttachment)
                    attachmentContent.append((char)c);
            }
            else System.out.write(c);
        }

        return attachmentContent.toString();
    }




}
