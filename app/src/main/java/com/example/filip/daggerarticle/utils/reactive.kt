import com.example.filip.daggerarticle.utils.DefaultError
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.disposables.Disposables
import io.reactivex.disposables.SerialDisposable
import org.funktionale.either.Either
import org.funktionale.option.Option
import org.funktionale.option.toOption
import retrofit2.HttpException
import java.lang.IllegalStateException
import java.net.SocketTimeoutException
import java.net.UnknownHostException

fun <T> Observable<T>.toEither(): Observable<Either<DefaultError, T>> = map<Either<DefaultError, T>> { Either.right(it) }.onErrorReturn { Either.left(it.toDefaultError()) }
fun <T> Observable<Either<DefaultError, T>>.successOrEmptyObservable(): Observable<T> = switchMap { it.fold({ Observable.empty<T>() }, { Observable.just<T>(it) }) }

fun Throwable.toDefaultError(): DefaultError = when (this) {
    is HttpException -> parseHttpException(this)
    is com.jakewharton.retrofit2.adapter.rxjava2.HttpException -> parseHttpException(this)
    is UnknownHostException -> NoInternetConnectionError
    is SocketTimeoutException -> TimeoutError
    is IllegalStateException -> IllegalStateError
    else -> BaseError
}

object BaseError : DefaultError

fun SerialDisposable.setFrom(vararg disposables: Disposable) = set(CompositeDisposable(disposables.asList()))

fun SerialDisposable.empty() = set(Disposables.empty())


internal fun parseHttpException(exception: HttpException): DefaultError = parseHttpExceptionCode(exception.code())

internal fun parseHttpException(exception: com.jakewharton.retrofit2.adapter.rxjava2.HttpException): DefaultError = parseHttpExceptionCode(exception.code())

private fun parseHttpExceptionCode(code: Int): DefaultError {
    return when (code) {
        in 300..306 -> RedirectionError("Code $code")
        in 401..401 -> UnauthorizedServerError
        in 401..402 -> ClientError("Code: $code", code)
        in 400..450 -> ClientError("Code: $code", code)
        in 500..598 -> ServerError("Code: $code")
        else -> UnknownServerError
    }
}


class ServerError(private val message: String) : DefaultError {
    override fun message(): Option<String> = message.toOption()
}

class ClientError(private val message: String,
                  private val errorCode: Int) : DefaultError {
    override fun message(): Option<String> = message.toOption()
    override fun code(): Option<Int> = errorCode.toOption()
}

class RedirectionError(private val message: String) : DefaultError {
    override fun message(): Option<String> = message.toOption()
}

class LogoutError(private val errorCode: Int) : DefaultError {
    override fun code(): Option<Int> = errorCode.toOption()
}

object UnknownServerError : DefaultError


object NoInternetConnectionError : DefaultError

object TimeoutError : DefaultError

object IllegalStateError : DefaultError

object UnauthorizedServerError : DefaultError