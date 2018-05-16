package org.hobbit.systems.wdaquawrapper;

import org.aksw.qa.commons.datastructure.IQuestion;
import org.aksw.qa.systems.QANARY;
import org.junit.Assert;
import org.junit.Test;

public class BasicQuestionTest {

	@Test
	public void test() throws Exception {
		QANARY qanary = new QANARY("http://wdaqua-core1.univ-st-etienne.fr/gerbil","dbpedia");
		qanary.setSetLangPar(false);
		IQuestion q = qanary.search("Who is Barack Obama's wife", "en");

		if (!q.getGoldenAnswers().contains("http://dbpedia.org/resource/Michelle_Obama")) {
			System.out.println(q.toString());
			Assert.fail();
		}
		;

	}

}
