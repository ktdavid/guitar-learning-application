package hu.unideb.inf.prt.guitarlearningapplication.controller;

import java.io.File;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import hu.unideb.inf.prt.guitarlearningapplication.Main;
import hu.unideb.inf.prt.guitarlearningapplication.helper.Helper;
import hu.unideb.inf.prt.guitarlearningapplication.model.Chord;
import hu.unideb.inf.prt.guitarlearningapplication.model.Chords;
import javafx.collections.FXCollections;
import javafx.scene.control.Alert.AlertType;

/**
 * Class for the controller of the GuitarNeckView.fxml.
 * 
 * @author Dávid Kistamás
 * @version 1.0
 */
public class IOController {
	
	/**
	 * A logger object used for logging events at runtime.
	 */
	private Logger logger = LoggerFactory.getLogger(IOController.class);
	
	/**
	 * A reference to the main application.
	 */
	private Main main;
	
	/**
	 * Constructs an empty {@code IOController} object.
	 */
	public IOController() {
	}
	
	/**
	 * It is called by the main application to give a reference back to itself.
	 * 
	 * @param main the main application
	 */
	public void setMainApp(Main main) {
		this.main = main;
	}
	
	/**
	 * Loads {@code Chord} objects from the specified file into an ObservableList for the {@code TableView}.
	 * 
	 * @param file the file from which the {@code Chord} objects will be loaded
	 */
	public void loadChordsFromFile(File file) {
		try {
			JAXBContext context = JAXBContext.newInstance(Chords.class);
			Unmarshaller um = context.createUnmarshaller();
			Chords wrapper = (Chords) um.unmarshal(file);
			main.getChordsForTableView().clear();
			main.getChordsForTableView().addAll(FXCollections.observableArrayList(wrapper.getChords()));
			
			if(main.isDebugSwitchEnabled()) {
				Marshaller marshaller = context.createMarshaller();
		        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
		        marshaller.marshal(wrapper, System.out);
			}

		} catch (JAXBException | IllegalArgumentException e) {
			Helper.createAlert("Error", "Could not load data from file:\n" + file.getPath(), AlertType.ERROR);
		}
	}
	
	/**
	 * Saves the actual chord to the specified file.
	 * 
	 * @param file the file which will contain the saved {@code Chord} objects
	 * @param chords the chords to be saved to the specified file
	 */
	public void saveChordToFile(File file, Chords chords) {
		try {
			JAXBContext context = JAXBContext.newInstance(Chords.class);
			Marshaller m = context.createMarshaller();
			m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
			m.marshal(chords, file);
			
			if(main.isDebugSwitchEnabled()) {
				m.marshal(chords, System.out);
			}
			
		} catch (JAXBException | IllegalArgumentException e) {
			Helper.createAlert("Error", "Could not save data to file:\n" + file.getPath(), AlertType.ERROR);
		}
	}
	
	/**
	 * Helper for appending the given {@code Chord} object to the XML file.
	 * 
	 * @param readyChord the {@code Chord} object to be saved
	 */
	public void saveChordAction(Chord readyChord) {
		if (main.getWrapperChordList() != null && main.getWrapperChordList().getChords() != null
				&& main.getWrapperChordList().getChords().stream().noneMatch(c -> c.getName().equals(readyChord.getName())
						&& c.getChordType().equals(readyChord.getChordType()))) {
			
			main.getWrapperChordList().add(readyChord);
			
			saveChordToFile(new File("chords.xml"), main.getWrapperChordList());
			
			logger.info("File has been saved successfully!");
		} else if (main.getWrapperChordList() == null || main.getWrapperChordList().getChords() == null) {
			main.getWrapperChordList().add(readyChord);
			saveChordToFile(new File("chords.xml"), main.getWrapperChordList());
			logger.info("File has been saved successfully!");
		} else {
			Helper.createAlert("Chord is saved before.",
					readyChord.getName() + " " + readyChord.getChordType() + " chord has been already saved before.", AlertType.ERROR);
		}
	}
}
