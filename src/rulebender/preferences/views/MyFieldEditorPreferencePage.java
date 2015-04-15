package rulebender.preferences.views;

import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.DirectoryFieldEditor;
import org.eclipse.ui.IWorkbench; 
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.eclipse.core.commands.AbstractHandler;
import rulebender.preferences.PreferencesClerk;
import rulebender.Activator;

public class MyFieldEditorPreferencePage extends FieldEditorPreferencePage implements IWorkbenchPreferencePage 
{
	
	public MyFieldEditorPreferencePage() 
	{
		super(GRID);
	}

	public void createFieldEditors() {
		System.out.println("Opening up the field editor");
		addField(new DirectoryFieldEditor("SIM_PATH", "&Path to simulator root (contains bin/):", getFieldEditorParent()));
		
		/*
		addField(new BooleanFieldEditor("BOOLEAN_VALUE", "&An example of a boolean preference", getFieldEditorParent()));

		addField(new RadioGroupFieldEditor("CHOICE", "An example of a multiple-choice preference", 1,
				new String[][] { { "&Choice 1", "choice1" }, { "C&hoice 2", "choice2" } }, getFieldEditorParent()));
		
		addField(new StringFieldEditor("MySTRING1", "A &text preference:",
				getFieldEditorParent()));
		
		addField(new StringFieldEditor("MySTRING2", "A &text preference:",
				getFieldEditorParent()));		
		*/
	}
	
	/*
	public String getUserBNGPath() {
		System.out.println("The user has selected " +
			 Activator.getDefault().getPreferenceStore().getDefaultString("SIM_PATH")
	);
		return  Activator.getDefault().getPreferenceStore().getDefaultString("SIM_PATH");
	}
	*/

	public void init(IWorkbench workbench) {
		setPreferenceStore(Activator.getDefault().getPreferenceStore());
		setDescription("RuleBender Preferences");
		
//		
//		    System.out.println("Setting default for  bngPath");
//			bngPath =  PreferencesClerk.getBNGRoot();
			Activator.getDefault().getPreferenceStore().setDefault("SIM_PATH",PreferencesClerk.getBNGRoot());


		/*
		System.out.println("" + System.getProperty("user.dir") + "/BioNetGen-2.2.0/");
		
		if(Activator.getDefault().getPreferenceStore().getString("SIM_PATH").equals(""))
		{
			System.out.println("Setting the default preference");
			Activator.getDefault().getPreferenceStore().setDefault("SIM_PATH", System.getProperty("user.dir") + "/BioNetGen-2.2.0/");
		}
		*/
	}

}
