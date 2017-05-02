package org.hobbit.systems.wdaquawrapper;

import java.io.IOException;
import java.util.List;

import org.aksw.qa.commons.datastructure.IQuestion;
import org.aksw.qa.commons.load.json.EJQuestionFactory;
import org.aksw.qa.commons.load.json.ExtendedQALDJSONLoader;
import org.aksw.qa.commons.load.json.QaldJson;
import org.aksw.qa.systems.QANARY;
import org.apache.jena.rdf.model.Literal;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.impl.PropertyImpl;
import org.apache.jena.rdf.model.impl.ResourceImpl;
import org.hobbit.core.components.AbstractSystemAdapter;
import org.hobbit.utils.rdf.RdfHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonProcessingException;

public class WdaquaSystemAdapter extends AbstractSystemAdapter {
	private static Logger LOGGER = LoggerFactory.getLogger(WdaquaSystemAdapter.class);
	public final static Resource WDAQUA_SYSTEM_URI = new ResourceImpl("http://w3id.org/hobbit/systems/wdaqua");
	public static final Property WDAQUA_URL = new PropertyImpl(WDAQUA_SYSTEM_URI.getURI() + "#url");
	private QANARY qanary;

	@Override
	public void init() throws Exception {
		super.init();

		qanary = new QANARY();

		Literal uri = RdfHelper.getLiteral(this.systemParamModel, WDAQUA_SYSTEM_URI, WDAQUA_URL);

		if (uri != null) {
			qanary.setQanaryUrl(uri.getString());
			LOGGER.info("Retrieved WDAQUA/QANARY URL from SystemTTL - " + uri);
		} else {
			LOGGER.info("No WDAQUA/QANARY URL in systemTTL defined - using default: " + qanary.getQanaryUrl());
		}

	}

	public void receiveGeneratedData(byte[] data) {

	}

	public void receiveGeneratedTask(String taskId, byte[] data) {

		QaldJson inputJson = null;
		try {
			inputJson = (QaldJson) ExtendedQALDJSONLoader.readJson(data, QaldJson.class);
		} catch (IOException e1) {
			LOGGER.error("Couldn't read input json", e1);
		}

		List<IQuestion> questions = EJQuestionFactory.getQuestionsFromQaldJson(inputJson);

		for (IQuestion it : questions) {
			// In QaldJson, a question can have multiple languages, but only one
			// answer set. So if we answer a question with multiple languages,
			// we don't know on which lkanguage the answer is based on.
			if (it.getLanguageToQuestion().entrySet().size() > 1) {

				LOGGER.info("Recieved Question with more than one language - using arbitrary");
			}
			try {
				qanary.search(it, it.getLanguageToQuestion().values().iterator().next());
			} catch (Exception e) {
				LOGGER.info("Couldn't answer question with YODA ", e);
			}
		}

		QaldJson answerJson = EJQuestionFactory.getQaldJson(questions);

		byte[] result = new byte[0];
		try {
			result = ExtendedQALDJSONLoader.writeJson(answerJson);
		} catch (JsonProcessingException e1) {
			// TODO handle malformed json
			e1.printStackTrace();
		}

		// Send the result to the evaluation storage
		try {
			sendResultToEvalStorage(taskId, result);
		} catch (IOException e) {
			// Log the error
		}
	}

	@Override
	public void close() throws IOException {
		qanary = null;
		super.close();
	}

}
