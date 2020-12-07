package com.github.sbaldin.greatadvice.ai

import com.github.sbaldin.greatadvice.ai.csv.AdviceLineIterator
import com.github.sbaldin.greatadvice.domain.asResource
import com.github.sbaldin.greatadvice.domain.getResourceAsFile
import org.deeplearning4j.models.word2vec.Word2Vec
import org.deeplearning4j.text.sentenceiterator.SentenceIterator
import org.deeplearning4j.text.tokenization.tokenizer.preprocessor.CommonPreprocessor
import org.deeplearning4j.text.tokenization.tokenizerfactory.DefaultTokenizerFactory
import org.deeplearning4j.text.tokenization.tokenizerfactory.TokenizerFactory
import org.slf4j.LoggerFactory
import java.io.File

/**
 * Created by agibsonccc on 10/9/14.
 *
 * Neural net that processes text into wordvectors. See below url for an in-depth explanation.
 * https://deeplearning4j.org/word2vec.html
 */
object Word2VecRawTextExample {
    private val log = LoggerFactory.getLogger(Word2VecRawTextExample::class.java)

    var dataLocalPath: String? = null
    private const val isWordToVecEnabled = false

    @JvmStatic
    fun main(args: Array<String>) {
        log.info("Load & Vectorize Sentences....")
        // Strip white space before and after for each line
        val sentenceIterator = AdviceLineIterator(getResourceAsFile("advice.csv"))

        // Split on white spaces in the line to get words
        val t: TokenizerFactory = DefaultTokenizerFactory()
        /*
        CommonPreprocessor will apply the following regex to each token: [\d\.:,"'\(\)\[\]|/?!;]+
        So, effectively all numbers, punctuation symbols and some special symbols are stripped off.
        Additionally it forces lower case for all tokens.
     */
        t.tokenPreProcessor = CommonPreprocessor()
        log.info("Building model....")
        val vec = Word2Vec.Builder()
            .minWordFrequency(2)
            .iterations(1)
            .layerSize(100)
            .seed(42)
            .windowSize(4)
            .iterate(sentenceIterator as SentenceIterator)
            .tokenizerFactory(t)
            .build()
        log.info("Fitting Word2Vec model....")
        vec.fit()
        log.info("Writing word vectors to text file....")
        // Prints out the closest 10 words to "day". An example on what to do with these Word Vectors.
        log.info("Closest Words:")
        val lst = vec.wordsNearestSum("говорить", 10)
        log.info("10 Words closest to 'говорить': {}", lst)

        // TODO resolve missing UiServer
//        UiServer server = UiServer.getInstance();
//        System.out.println("Started on port " + server.getPort());
    }
}

