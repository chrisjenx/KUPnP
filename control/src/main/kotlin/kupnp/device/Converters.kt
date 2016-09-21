package kupnp.device

import org.simpleframework.xml.core.Persister
import org.simpleframework.xml.stream.Format
import org.simpleframework.xml.stream.Verbosity
import retrofit2.converter.simplexml.SimpleXmlConverterFactory

/**
 * Created by chris on 21/09/2016.
 */


val xmlConverter: SimpleXmlConverterFactory  by lazy {
    val persister = Persister(Format(0, Verbosity.HIGH))
    SimpleXmlConverterFactory.createNonStrict(persister)
}