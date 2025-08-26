const ErrorMessage = ({
  title = '에러가 발생했습니다',
  message,
  detail,
  onRetry,
  className = '',
}) => {
  return (
    <div
      role="alert"
      className={`rounded-md border border-red-200 bg-red-50 p-4 text-red-700 ${className}`}
    >
      <div className="flex items-start gap-3">
        <span aria-hidden className="mt-0.5 inline-block h-5 w-5">
          ⚠️
        </span>
        <div className="flex-1">
          <p className="font-semibold">{title}</p>
          {message && <p className="mt-1 text-sm">{String(message)}</p>}
          {detail && (
            <pre className="mt-2 whitespace-pre-wrap text-xs text-red-600">
              {detail}
            </pre>
          )}
          {onRetry && (
            <button
              type="button"
              onClick={onRetry}
              className="mt-3 inline-flex items-center rounded-md border border-red-300 px-3 py-1.5 text-sm hover:bg-white"
            >
              다시 시도
            </button>
          )}
        </div>
      </div>
    </div>
  );
};

export default ErrorMessage;
