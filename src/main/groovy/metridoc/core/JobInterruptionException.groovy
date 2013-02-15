package metridoc.core

/**
 * Created with IntelliJ IDEA.
 * User: tbarker
 * Date: 2/15/13
 * Time: 2:27 PM
 * To change this template use File | Settings | File Templates.
 */
class JobInterruptionException extends Exception{

    JobInterruptionException(String jobName) {
        super("Job ${jobName} was manually interrupted")
    }

}
