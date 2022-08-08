package classifier.trainer;

import java.io.File;
import java.util.*;

import classifier.process.DataReader;
import fx.satds_fx.model.Comment;
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
	private static String rscdir = (System.getProperty("user.dir") +"\\satds_fx\\src\\main\\resources\\").replace("\\", File.separator);
	// if 3 out of 8 classifiers think it is a SATD, retain this
	private static final double VOTE_THRESHOLD = 3.0;
	private static List<String> projects = new ArrayList<String>(Arrays.asList(
			"argouml", "columba-1.4-src","hibernate-distribution-3.3.2.GA",
			"jEdit-4.2", "jfreechart-1.0.19","apache-jmeter-2.10","jruby-1.4.0",
			"sql12"
	));

	// this class will retrain the .stw, .slc, .model files of the projects listed above.
	public static void retrain() throws Exception {
		// feature selection ratio
		double ratio = 0.1;

		List<Document> comments = DataReader.readComments(rscdir + "traindata\\".replace("\\", File.separator) );

		for (int source = 0; source < projects.size(); source++) {
			String projectName = projects.get(source);

			Set<String> projectForTraining = new HashSet<String>();
			projectForTraining.add(projectName);

			// trainDoc: all comments from one project
			List<Document> trainDoc = DataReader.selectProject(comments, projectForTraining);

			String trainingDataPath = rscdir + "traindata\\tmpTrainingData.arff".replace("\\", File.separator);
			DataReader.outputArffData(trainDoc, trainingDataPath);

//			// put data into column format for coreNLP classifier
//			String columnDataPath = rscdir + "traindata\\".replace("\\", File.separator) + projects.get(source) + ".train";
//			DataReader.outputColumnData(trainDoc, columnDataPath);

			// get StringToWordVector object
			/* Converts string attributes into a set of numeric attributes representing word
			   occurrence information from the text contained in the strings. */
			StringToWordVector stw = new StringToWordVector(100000);
			// stw output word counts, rather than 0/1 representing appeared or not
			stw.setOutputWordCounts(true);
			// stw transforms word frequency into: fij*log(num of Docs/num of Docs with word i)
			/* IDF: Inverse Document Frequency
			    How common or rare a word is. */
			stw.setIDFTransform(true);
			// stw transforms word frequency into: log(1+fij)
			/* TF: Term Frequency
			    How frequently does a word appear in a document. */
			stw.setTFTransform(true);
			// stw uses SnowballStemmer
			SnowballStemmer stemmer = new SnowballStemmer();
			stw.setStemmer(stemmer);
			// stw filter the stopwords
			WordsFromFile stopwords = new WordsFromFile();
			stopwords.setStopwords(new File(rscdir + "traindata\\stopwords.txt".replace("\\", File.separator)));
			stw.setStopwordsHandler(stopwords);
			// filter the training data to create dictionary
			Instances trainSet = DataSource.read(trainingDataPath);
			stw.setInputFormat(trainSet);
			trainSet = Filter.useFilter(trainSet, stw);
			trainSet.setClassIndex(0);
			// serialize StringToWordVector
			SerializationHelper.write(rscdir + "classifiers\\".replace("\\", File.separator) + projectName + ".stw", stw);

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
			SerializationHelper.write(rscdir + "classifiers\\".replace("\\", File.separator) + projectName + ".slc", attSelection);

			// get classifier object
			Classifier classifier = new NaiveBayesMultinomial();
			classifier.buildClassifier(trainSet);
			// serialize classifier
			SerializationHelper.write( rscdir + "classifiers\\".replace("\\", File.separator) + projectName + ".model", classifier );

			System.out.println(projectName + " training finished");
		}
		System.out.println("All training finished");
	}

	public static List<Long> classify(List<Comment> commentList) throws Exception {

		// read comments, tokenize and put the results are in Document.words
		List<Document> targetDoc = DataReader.readComments( commentList );
		// put tokens into arff file
		String targetDataPath = rscdir + "traindata\\targetData.arff".replace("\\", File.separator);
		DataReader.outputArffData(targetDoc, targetDataPath);

		// read arff file as Instances
		Instances tmp = DataSource.read(targetDataPath);
		tmp.setClassIndex(1);
		EnsembleLearner eLearner = new EnsembleLearner(tmp);

		// go through each classifier and get vote
		for (int source = 0; source < projects.size(); source++) {

			String projectName = projects.get(source);

			// string to word vector
			// the stw contains stopwords and stemmer
			Instances tarSet = DataSource.read(targetDataPath);
			StringToWordVector stw = (StringToWordVector)SerializationHelper.read(rscdir + "classifiers\\".replace("\\", File.separator) + projectName + ".stw");
			tarSet = Filter.useFilter(tarSet, stw);
			tarSet.setClassIndex(0);

			// attribute selection
			AttributeSelection attSelection = (AttributeSelection)SerializationHelper.read( rscdir + "classifiers\\".replace("\\", File.separator) + projectName + ".slc" );
			tarSet = Filter.useFilter(tarSet, attSelection);

			// classifier
			Classifier classifier = (Classifier) SerializationHelper.read( rscdir + "classifiers\\".replace("\\", File.separator) + projectName + ".model" );

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

	// classify without ensemble method
	// public static List<Long> classifyWithOneClassifier(List<Comment> commentList) throws Exception {

	// 	// read comments, tokenize and put the results are in Document.words
	// 	List<Document> targetDoc = DataReader.readComments( commentList );
	// 	// put tokens into arff file
	// 	String targetDataPath = rscdir + "traindata\\targetData.arff".replace("\\", File.separator);
	// 	DataReader.outputArffData(targetDoc, targetDataPath);

	// 	// read arff file as Instances
	// 	Instances tmp = DataSource.read(targetDataPath);
	// 	tmp.setClassIndex(1);
	// 	EnsembleLearner eLearner = new EnsembleLearner(tmp);


	// 	// string to word vector
	// 	// the stw contains stopwords and stemmer
	// 	Instances tarSet = DataSource.read(targetDataPath);
	// 	StringToWordVector stw = (StringToWordVector)SerializationHelper.read(rscdir + "classifiers\\total.stw".replace("\\", File.separator));
	// 	tarSet = Filter.useFilter(tarSet, stw);
	// 	tarSet.setClassIndex(0);

	// 	// attribute selection
	// 	AttributeSelection attSelection = (AttributeSelection)SerializationHelper.read( rscdir + "classifiers\\total.slc".replace("\\", File.separator) );
	// 	tarSet = Filter.useFilter(tarSet, attSelection);

	// 	// classifier
	// 	Classifier classifier = (Classifier) SerializationHelper.read( rscdir + "classifiers\\total.model".replace("\\", File.separator) );

	// 	for (int i = 0; i < tarSet.numInstances(); i++) {
	// 		// assert: instance has the same order with commentList
	// 		Instance instance = tarSet.instance(i);
	// 		if (classifier.classifyInstance(instance) == 1.0)
	// 			eLearner.vote(i, 1.0 );
	// 	}


	// 	// remove negative vote comments from dataset
	// 	double[] votes = eLearner.getVote();
	// 	List<Long> tobeRemove = new ArrayList<>();
	// 	// ^^^^ breakpoint here to see the score and the comments
	// 	for( int i = 0; i < votes.length; ++i ) {
	// 		if( votes[i] < 1 )
	// 			tobeRemove.add( Long.valueOf( i ) );
	// 	}
	// 	return tobeRemove;
	// }

}
