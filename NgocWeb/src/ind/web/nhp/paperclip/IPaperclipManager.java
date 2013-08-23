package ind.web.nhp.paperclip;

public interface IPaperclipManager {

    public String createAttachment(IPaperclip paperclip);

    public IPaperclip updateAttachment(IPaperclip paperclip);

    public void deleteAttachment(IPaperclip paperclip);

    public IPaperclip getAttachment(String id);
}
