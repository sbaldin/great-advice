package com.github.sbaldin.greatadvice.ai.stem

import org.deeplearning4j.text.sentenceiterator.SentenceIterator
import ru.stachek66.nlp.mystem.holding.Factory
import ru.stachek66.nlp.mystem.holding.MyStem
import ru.stachek66.nlp.mystem.holding.MyStemApplicationException
import ru.stachek66.nlp.mystem.holding.Request
import ru.stachek66.nlp.mystem.model.Info
import scala.Option
import scala.collection.JavaConversions

/**
 * -i Печатать грамматическую информацию, расшифровка ниже.
 * -g Склеивать информацию словоформ при одной лемме (только при включенной опции -i).
 * -d	Применить контекстное снятие омонимии.
 * --eng-gr Печатать английские обозначения граммем.
 * --format Формат вывода. Возможные варианты: text, xml, json. Значение по умолчанию — text.
 * --weight	Печатать бесконтекстную вероятность леммы.
 * @see  https://yandex.ru/dev/mystem/doc/
 */
fun getYandexStemmer(): MyStem {
    return Factory("-igd --eng-gr --format json --weight").newMyStem(
        "3.1",
        Option.empty()
    ).get()
}