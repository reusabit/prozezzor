import ch.qos.logback.classic.filter.ThresholdFilter
import com.reusabit.prozezzor.LocalStorageService

def lfs = new LocalStorageService()
def logdir = lfs.prepareDirectory("log")
def logdirString = logdir.getPath()

appender("FILE", RollingFileAppender){
    rollingPolicy(TimeBasedRollingPolicy) {
        fileNamePattern = "${logdirString}/log-%d.txt"
        maxHistory = 30
        totalSizeCap = "100MB"
        cleanHistoryOnStart = true
    }
    encoder(PatternLayoutEncoder) {
        pattern = "%date{yyyy-MM-dd HH:mm:ss.SSS} %-5level %-25logger{25}: %message%n%exception"
    }
}

appender("CONSOLE", ConsoleAppender){
    encoder(PatternLayoutEncoder) {
        pattern = "%message%n %exception"
    }
    filter(ThresholdFilter) {
        level = INFO
    }
}

root(DEBUG, ["FILE", "CONSOLE"])
