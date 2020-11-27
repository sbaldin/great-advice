package com.github.sbaldin.greatadvice.ai


//import org.deeplearning4j.examples.download.DownloaderUtility
import org.deeplearning4j.models.word2vec.Word2Vec
import org.deeplearning4j.text.sentenceiterator.BasicLineIterator
import org.deeplearning4j.text.sentenceiterator.SentenceIterator
import org.deeplearning4j.text.tokenization.tokenizer.preprocessor.CommonPreprocessor
import org.deeplearning4j.text.tokenization.tokenizerfactory.DefaultTokenizerFactory
import org.deeplearning4j.text.tokenization.tokenizerfactory.TokenizerFactory
import org.slf4j.LoggerFactory
import ru.stachek66.nlp.mystem.holding.Factory
import ru.stachek66.nlp.mystem.holding.MyStem
import ru.stachek66.nlp.mystem.holding.MyStemApplicationException
import ru.stachek66.nlp.mystem.holding.Request
import ru.stachek66.nlp.mystem.model.Info
import scala.Option
import scala.collection.JavaConversions
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
        dataLocalPath = ""//DownloaderUtility.NLPDATA.Download()
        // Gets Path to Text file
        val filePath = File(dataLocalPath, "advice_sentence.txt").absolutePath
        log.info("Load & Vectorize Sentences....")
        // Strip white space before and after for each line
        val iter: SentenceIterator = BasicLineIterator(filePath)
        val stm = StemSentenceProcessor(iter)
        stm.process()
        if (isWordToVecEnabled) {
            // Split on white spaces in the line to get words
            val t: TokenizerFactory = DefaultTokenizerFactory()
            /*
            CommonPreprocessor will apply the following regex to each token: [\d\.:,"'\(\)\[\]|/?!;]+
            So, effectively all numbers, punctuation symbols and some special symbols are stripped off.
            Additionally it forces lower case for all tokens.
         */t.tokenPreProcessor = CommonPreprocessor()
            log.info("Building model....")
            val vec = Word2Vec.Builder()
                .minWordFrequency(5)
                .iterations(1)
                .layerSize(100)
                .seed(42)
                .windowSize(5)
                .iterate(iter)
                .tokenizerFactory(t)
                .build()
            log.info("Fitting Word2Vec model....")
            vec.fit()
            log.info("Writing word vectors to text file....")
            // Prints out the closest 10 words to "day". An example on what to do with these Word Vectors.
            log.info("Closest Words:")
            val lst = vec.wordsNearestSum("Сделай", 10)
            log.info("10 Words closest to 'думай': {}", lst)
        }
        // TODO resolve missing UiServer
//        UiServer server = UiServer.getInstance();
//        System.out.println("Started on port " + server.getPort());
    }
}

class StemSentenceProcessor(
    private val iter: SentenceIterator,
    private val mystemAnalyzer: MyStem = getDefaultStemmer()
) {

    @Throws(MyStemApplicationException::class)
    fun process() {
        while (iter.hasNext()) {
            val advice = iter.nextSentence()
            val result: Iterable<Info> = JavaConversions.asJavaIterable(
                mystemAnalyzer
                    .analyze(Request.apply(advice))
                    .info()
                    .toIterable()
            )
            for (info in result) {
                println(info.initial().toString() + " -> " + info.lex() + " | " + info.rawResponse())
            }
        }
    }
}

/**
 * -i Печатать грамматическую информацию, расшифровка ниже.
 * -g Склеивать информацию словоформ при одной лемме (только при включенной опции -i).
 * -d	Применить контекстное снятие омонимии.
 * --eng-gr Печатать английские обозначения граммем.
 * --format Формат вывода. Возможные варианты: text, xml, json. Значение по умолчанию — text.
 * --weight	Печатать бесконтекстную вероятность леммы.
 * @see  https://yandex.ru/dev/mystem/doc/
 */
private fun getDefaultStemmer(): MyStem {
    return Factory("-igd --eng-gr --format json --weight").newMyStem(
        "3.0",
        Option.empty()
    ).get()
}