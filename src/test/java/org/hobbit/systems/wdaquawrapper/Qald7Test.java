package org.hobbit.systems.wdaquawrapper;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

import org.aksw.qa.commons.datastructure.IQuestion;
import org.aksw.qa.commons.load.Dataset;
import org.aksw.qa.commons.load.LoaderController;
import org.aksw.qa.commons.load.json.EJQuestionFactory;
import org.aksw.qa.commons.load.json.ExtendedQALDJSONLoader;
import org.aksw.qa.commons.load.json.QaldJson;
import org.junit.Ignore;
import org.junit.Test;

@Ignore
public class Qald7Test {

	@Test

	public void test() throws Exception {
		WdaquaSystemAdapter adapt = new WdaquaSystemAdapter();
		adapt.init();
		List<IQuestion> questions = LoaderController.load(Dataset.QALD7_Train_Multilingual);
		for (IQuestion q : questions) {
			HashSet<String> languageSet = new HashSet<>(q.getLanguageToQuestion().keySet());
			languageSet.remove("en");
			q.setGoldenAnswers(new HashSet<String>());
			for (String it : languageSet) {
				q.getLanguageToQuestion().remove(it);
			}
			QaldJson questionJson = EJQuestionFactory.getQaldJson(new ArrayList<>(Arrays.asList(q)));
			System.out.println("asking question : " + q.getLanguageToQuestion().get("en"));
			try {
				adapt.receiveGeneratedTask("asd", ExtendedQALDJSONLoader.writeJson(questionJson));
			} catch (Exception e) {
				e.printStackTrace();
			}

		}
	}

}
