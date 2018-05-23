/*
 *  This is the code for read the unread mails from your mail account.
 *  Requirements:
 *      JDK 1.5 and above
 *      Jar:mail.jar
 *
 */

import java.io.*;
import java.util.*;
import javax.mail.*;
import javax.mail.Flags.Flag;
import javax.mail.search.FlagTerm;

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
        //Set the mail properties
        Properties props = System.getProperties();
        props.setProperty("mail.store.protocol", "imaps");
        try
        {
            //Create the session and get the store for read the mail.
            Session session = Session.getDefaultInstance(props, null);
            Store store = session.getStore("imaps");
            store.connect("imap-mail.outlook.com", userEmail, userPassword);


            //Mention the folder name which you want to read.
            inbox = store.getFolder(folderName);
            System.out.println("No of Unread Messages : " + inbox.getUnreadMessageCount());

            //Open the inbox using store.
            inbox.open(Folder.READ_ONLY);

            //Get the messages which is unread in the Inbox
            Message messages[] = inbox.search(new FlagTerm(new Flags(Flag.SEEN), false));

            /* Use a suitable FetchProfile    */
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
                System.out.println("Exception arise at the time of read mail");
                ex.printStackTrace();
            }
        }
        catch (NoSuchProviderException e)
        {
            e.printStackTrace();
            System.exit(1);
        }
        catch (MessagingException e)
        {
            e.printStackTrace();
            System.exit(2);
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
        // TO
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
        //System.out.println("\n" + reportName + "\n");
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
            ex.printStackTrace();
        }
    }

    public String getAttachment(Part p) throws Exception
    {
        StringBuilder attachmentContent = new StringBuilder();
        //System.out.println("IN DUMP PART");
        // Dump input stream ..
        InputStream is = p.getInputStream();
        // If "is" is not already buffered, wrap a BufferedInputStream
        // around it.
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
        //System.out.println(attachmentContent.toString());
        return attachmentContent.toString();
    }
}
