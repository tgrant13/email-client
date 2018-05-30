import java.io.*;
import java.util.*;
import javax.mail.*;
import javax.mail.Flags.Flag;
import javax.mail.search.FlagTerm;
import javax.swing.JOptionPane;


public class OutlookReader
{
    private Folder inbox;
    private String folderName;
    private boolean debug;
    private HashMap<String, String> fileNamesPlusAttachments;
    private String userEmail;
    private String userPassword;

    public OutlookReader(String email_in, String password_in)
    {
        debug = false;
        fileNamesPlusAttachments = new HashMap<>();
        userEmail = email_in;
        userPassword = password_in;
        folderName = "Splunk Reports";
    }

    public HashMap<String, String> retrieveEmails()
    {
        fileNamesPlusAttachments = new HashMap<String, String>();

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



            inbox = store.getFolder(folderName);
            System.err.println("No of Unread Messages : " + inbox.getUnreadMessageCount());


            inbox.open(Folder.READ_ONLY);


            Message messages[] = inbox.search(new FlagTerm(new Flags(Flag.SEEN), false));


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

    public void printAllMessages(Message[] msgs) throws Exception
    {
        for (int i = 0; i < msgs.length; i++)
        {
            Address[] FROMaddr = msgs[i].getFrom();
            if(debug && FROMaddr != null)
            {
                System.out.println("-------------------------------------------------------");
                String nameAddr = FROMaddr[0].toString();
                System.out.println("NAME: " + nameAddr);
                System.out.println("-------------------------------------------------------");
            }


            if(FROMaddr != null && FROMaddr[0].toString().contains("Tyler Grant <turboespanol@gmail.com>"))
            {
                printEnvelope(msgs[i]);
            }
        }
    }

    /*  Print the envelope(FromAddress,ReceivedDate,Subject)  */
    public void printEnvelope(Message message) throws Exception
    {
        Address[] a;

        if ((a = message.getFrom()) != null)
        {
            for (int j = 0; j < a.length; j++)
            {
                if(debug) System.out.println("FROM: " + a[j].toString());
            }
        }

        if ((a = message.getRecipients(Message.RecipientType.TO)) != null)
        {
            for (int j = 0; j < a.length; j++)
            {
                if(debug) System.out.println("TO: " + a[j].toString());
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

    public void getContent(String reportName, Message msg)
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

    public String getAttachment(Part p) throws Exception
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
