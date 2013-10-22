package metridoc.stream

/**
 * Created with IntelliJ IDEA on 10/22/13
 * @author Tommy Barker
 */
abstract class BaseExcelStream extends FileStream<Map> {
    /**
     * if a sheet name is specified, it trumps any sheet index if specified
     */
    String sheetName
    /**
     * default is 0, will be ignored if a {@link BaseExcelStream#sheetName} is specified
     */
    int sheetIndex = 0

    @SuppressWarnings("GroovyAssignabilityCheck")
    protected static int convertColumnToNumber(String column) {
        def m = (column =~ /(\D+)\d*/)  //strip out record numbers
        m.find()
        def justColumn = m.group(1)

        String lowercase = justColumn.toLowerCase()

        def columnNumber = 0
        int a = 'a'

        for (int i = 0; i < lowercase.size(); i++) {
            int power = lowercase.size() - i - 1
            int charRelativeTo_a = lowercase.charAt(i) - a + 1
            def charInBase26 = (charRelativeTo_a) * (26 ** power)
            columnNumber += charInBase26
        }

        return columnNumber
    }
}
