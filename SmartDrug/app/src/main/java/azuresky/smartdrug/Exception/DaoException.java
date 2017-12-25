package azuresky.smartdrug.Exception;

/**
 * Created by User on 2015/11/29.
 */
public class DaoException extends Exception {
    public DaoException() {
    }

    public DaoException(String detailMessage) {
        super(detailMessage);
    }

    public DaoException(String detailMessage, Throwable throwable) {
        super(detailMessage, throwable);
    }

    public DaoException(Throwable throwable) {
        super(throwable);
    }
}
