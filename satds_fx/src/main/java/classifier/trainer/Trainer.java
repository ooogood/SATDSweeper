package classifier.trainer;

import java.io.File;
import java.util.*;

import classifier.process.DataReader;
import fx.satds_fx.Comment;
import weka.attributeSelection.InfoGainAttributeEval;
import weka.attributeSelection.Ranker;
import weka.classifiers.Classifier;
import weka.classifiers.bayes.NaiveBayesMultinomial;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.converters.ConverterUtils.DataSource;
import weka.core.stemmers.SnowballStemmer;
import weka.core.stopwords.WordsFromFile;
import weka.filters.Filter;
import weka.filters.supervised.attribute.AttributeSelection;
import weka.filters.unsupervised.attribute.StringToWordVector;
import weka.core.SerializationHelper;
import classifier.domain.Document;
import classifier.model.EnsembleLearner;

public class Trainer {
	private static String rscdir = System.getProperty("user.dir") +"\\satds_fx\\src\\main\\resources\\";
	// if 3 out of 8 classifiers think it is a SATD, retain this
	private static final double VOTE_THRESHOLD = 3.0;

	public static void retrain() throws Exception {

		List<String> projects = new ArrayList<String>();

		projects.add("argouml");
		projects.add("columba-1.4-src");
		projects.add("hibernate-distribution-3.3.2.GA");
		projects.add("jEdit-4.2");
		projects.add("jfreechart-1.0.19");
		projects.add("apache-jmeter-2.10");
		projects.add("jruby-1.4.0");
		projects.add("sql12");
		
		double ratio = 0.1;

		List<Document> comments = DataReader.readComments(rscdir + "traindata\\" );

		for (int source = 0; source < projects.size(); source++) {


			Set<String> projectForTraining = new HashSet<String>();
			projectForTraining.add(projects.get(source));

			// trainDoc: all comments from one project
			List<Document> trainDoc = DataReader.selectProject(comments, projectForTraining);

			String trainingDataPath = rscdir + "traindata\\tmpTrainingData.arff";
			DataReader.outputArffData(trainDoc, trainingDataPath);

			// get StringToWordVector object
			StringToWordVector stw = new StringToWordVector(100000);
			stw.setOutputWordCounts(true);
			stw.setIDFTransform(true);
			stw.setTFTransform(true);
			SnowballStemmer stemmer = new SnowballStemmer();
			stw.setStemmer(stemmer);
			WordsFromFile stopwords = new WordsFromFile();
			stopwords.setStopwords(new File(rscdir + "traindata\\stopwords.txt"));
			stw.setStopwordsHandler(stopwords);
			Instances trainSet = DataSource.read(trainingDataPath);
			stw.setInputFormat(trainSet);
			trainSet = Filter.useFilter(trainSet, stw);
			trainSet.setClassIndex(0);
			// serialize StringToWordVector
			SerializationHelper.write(rscdir + "classifiers\\" + projects.get(source) + ".stw", stw);

			// get attributeSelection object
			AttributeSelection attSelection = new AttributeSelection();
			Ranker ranker = new Ranker();
			ranker.setNumToSelect((int) (trainSet.numAttributes() * ratio));
			InfoGainAttributeEval ifg = new InfoGainAttributeEval();
			attSelection.setEvaluator(ifg);
			attSelection.setSearch(ranker);
			attSelection.setInputFormat(trainSet);
			trainSet = Filter.useFilter(trainSet, attSelection);
			// serialize attributeSelection
			SerializationHelper.write(rscdir + "classifiers\\" + projects.get(source) + ".slc", attSelection);

			// get classifier object
			Classifier classifier = new NaiveBayesMultinomial();
			classifier.buildClassifier(trainSet);
			// serialize classifier
			SerializationHelper.write( rscdir + "classifiers\\" + projects.get(source) + ".model", classifier );

			System.out.println(projects.get(source) + " training finished");
		}
		System.out.println("All training finished");
	}

	public static List<Long> classify(List<Comment> commentList) throws Exception {

		List<String> projects = new ArrayList<String>();
		projects.add("argouml");
		projects.add("columba-1.4-src");
		projects.add("hibernate-distribution-3.3.2.GA");
		projects.add("jEdit-4.2");
		projects.add("jfreechart-1.0.19");
		projects.add("apache-jmeter-2.10");
		projects.add("jruby-1.4.0");
		projects.add("sql12");

		List<Document> targetDoc = DataReader.readComments( commentList );
		String targetDataPath = rscdir + "traindata\\targetData.arff";
		DataReader.outputArffData(targetDoc, targetDataPath);

		Instances tmp = DataSource.read(targetDataPath);
		tmp.setClassIndex(1);
		EnsembleLearner eLearner = new EnsembleLearner(tmp);

		for (int source = 0; source < projects.size(); source++) {
			// assert: the following process will not change .arff file
			// string to word vector (both for training and testing data)
			Instances tarSet = DataSource.read(targetDataPath);
			StringToWordVector stw = (StringToWordVector)SerializationHelper.read(rscdir + "classifiers\\" + projects.get(source) + ".stw");
			tarSet = Filter.useFilter(tarSet, stw);
			tarSet.setClassIndex(0);

			// attribute selection
			AttributeSelection attSelection = (AttributeSelection)SerializationHelper.read( rscdir + "classifiers\\" + projects.get(source) + ".slc" );
			tarSet = Filter.useFilter(tarSet, attSelection);

			// classifier
			Classifier classifier = (Classifier) SerializationHelper.read( rscdir + "classifiers\\" + projects.get(source) + ".model" );

			for (int i = 0; i < tarSet.numInstances(); i++) {
				// assert: instance has the same order with commentList
				Instance instance = tarSet.instance(i);
				if (classifier.classifyInstance(instance) == 1.0)
					eLearner.vote(i, 1.0 );
			}

		}

		// remove negative vote comments from dataset
		double[] votes = eLearner.getVote();
		List<Long> tobeRemove = new ArrayList<>();
		// ^^^^ breakpoint here to see the score and the comments
		for( int i = 0; i < votes.length; ++i ) {
			if( votes[i] < VOTE_THRESHOLD )
				tobeRemove.add( Long.valueOf( i ) );
		}
		return tobeRemove;
	}

}
