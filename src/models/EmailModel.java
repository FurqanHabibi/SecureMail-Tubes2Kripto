package models;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class EmailModel implements Serializable{

	private List<FolderModel> folders;
	
	public EmailModel() {
		folders = new ArrayList<FolderModel>();
	}
	
	public void addFolderModel(FolderModel fm) {
		folders.add(fm);
	}
	
	public List<FolderModel> getFolders() {
		return folders;
	}
}
