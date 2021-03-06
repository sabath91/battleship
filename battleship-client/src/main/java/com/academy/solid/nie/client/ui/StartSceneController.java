package com.academy.solid.nie.client.ui;


import com.academy.solid.nie.client.communication.IpValidator;
import com.academy.solid.nie.client.communication.SocketServer;
import com.academy.solid.nie.client.communication.Validator;
import com.academy.solid.nie.client.config.ConfigProperty;
import com.academy.solid.nie.client.config.Configuration;
import com.academy.solid.nie.client.config.FileConfiguration;
import com.academy.solid.nie.client.language.Message;
import com.academy.solid.nie.client.language.MessageProviderImpl;
import com.academy.solid.nie.client.language.Language;
import com.academy.solid.nie.client.output.Output;
import com.academy.solid.nie.client.output.OutputFactory;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;

import java.io.IOException;


/**
 * Controller to manage components of the starting window.
 */
public final class StartSceneController {
    @FXML
    private ImageView logo;
    @FXML
    private Button btnConnect;
    @FXML
    private Label insertIpLabel;
    @FXML
    private TextField fieldIP;
    @FXML
    private TextField fieldPort;
    @FXML
    private Label nameInputLabel;
    @FXML
    private TextField nameInput;
    @FXML
    private ChoiceBox<Language> languageChoice;
    private ObservableList<Language> availableLanguages = FXCollections.
            observableArrayList(Language.POLISH, Language.ENGLISH);
    private MessageProviderImpl communicateProvider;
    private Output output;

    @FXML
    void initialize() {
        Configuration configuration = new FileConfiguration();
        configuration.provide();
        String language = configuration.getCommunicate(ConfigProperty.LANGUAGE);
        Language defaultLanguage = provideLanguage(language);
        initializeLanguageMenu(defaultLanguage);
        initializeIpField(configuration.getCommunicate(ConfigProperty.SERVER_IP));
        initializePortField(configuration.getCommunicate(ConfigProperty.SERVER_PORT));
        output = new OutputFactory(configuration).create();
    }

    private Language provideLanguage(String providedLanguage) {
        Language language;
        language = Language.valueOf(providedLanguage.toUpperCase());
        return language;
    }

    private void initializePortField(String communicate) {
        fieldPort.setText(communicate);
    }

    private void initializeIpField(String communicate) {
        fieldIP.setText(communicate);
    }

    @FXML
    void btnConnectClicked(final ActionEvent event) throws IOException {
        Validator ipValidator = new IpValidator();
        Validator portValidator = new PortValidator();
        Validator connectionValidator = new ConnectionValidator();
        String ip = fieldIP.getText();
        String port = fieldPort.getText();
        String name = nameInput.getText();
        if (ipValidator.validate(ip) && portValidator.validate(port)) {
            SocketServer socketServer = new SocketServer();
            if (!connectionValidator.validate(ip)) {
                WindowDisplayer wrongIpWindow = new WindowDisplayer(
                        MessageProviderImpl.getCommunicate(Message.CONNECTION_PROBLEM))
                        .withButtonWhoExitThisWindow();
                wrongIpWindow.display();
            } else {
                socketServer.connect(ip, Integer.parseInt(port));
                GameScene gameScene = new GameScene(socketServer, output, name);
                gameScene.start();
                new Thread(gameScene).start();
            }
        } else {
            WindowDisplayer wrongIpWindow = new WindowDisplayer(
                    MessageProviderImpl.getCommunicate(Message.WRONG_INPUT))
                    .withButtonWhoExitThisWindow();
            wrongIpWindow.display();
        }
    }

    @FXML
    void languageChosen(ActionEvent actionEvent) {
        if (languageChoice.getValue() == Language.POLSKI || languageChoice.getValue() == Language.POLISH) {
            availableLanguages = FXCollections.
                    observableArrayList(Language.POLSKI, Language.ANGIELSKI);
            communicateProvider.populate(Language.POLISH);
            languageChoice.setItems(availableLanguages);
            languageChoice.setValue(Language.POLSKI);
        } else if (languageChoice.getValue() == Language.ANGIELSKI) {
            availableLanguages = FXCollections.
                    observableArrayList(Language.POLISH, Language.ENGLISH);
            communicateProvider.populate(Language.ENGLISH);
            languageChoice.setItems(availableLanguages);
            languageChoice.setValue(Language.ENGLISH);
        } else {
            availableLanguages = FXCollections.
                    observableArrayList(Language.POLISH, Language.ENGLISH);
            communicateProvider.populate(Language.ENGLISH);
            languageChoice.setValue(Language.ENGLISH);
        }
        updateFields();
    }

    private void initializeLanguageMenu(Language defaultLanguage) {
        communicateProvider = new MessageProviderImpl();
        communicateProvider.populate(defaultLanguage);
        languageChoice.setItems(availableLanguages);
        languageChoice.setValue(defaultLanguage);
    }

    private void updateFields() {
        nameInputLabel.setText(MessageProviderImpl.getCommunicate(Message.NAME));
        insertIpLabel.setText(MessageProviderImpl.getCommunicate(Message.INSERT_IP));
        btnConnect.setText(MessageProviderImpl.getCommunicate(Message.CONNECT));
    }
}
