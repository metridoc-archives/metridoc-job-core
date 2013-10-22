package metridoc.stream

import com.google.common.collect.AbstractIterator
import metridoc.utils.IOUtils

/**
 * Created with IntelliJ IDEA on 10/22/13
 * @author Tommy Barker
 */
abstract class FileStream<T> extends AbstractIterator<T> {
    String path

    @Lazy(soft = true)
    File file = {
        if (path) {
            return new File(path)
        }

        return null
    }()

    @Lazy
    String fileName = {
        if (file) {
            return file.name
        }

        return null
    }()

    @Lazy(soft = true)
    InputStream inputStream = {
        assert file : "file needs to be set in FileIterator"
        file.newInputStream()
    }()

    void close() {
        IOUtils.closeQuietly(inputStream)
    }
}
