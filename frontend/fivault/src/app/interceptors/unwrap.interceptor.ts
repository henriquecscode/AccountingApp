import { HttpInterceptorFn, HttpErrorResponse, HttpResponse } from '@angular/common/http';
import { throwError } from 'rxjs';
import { map, catchError } from 'rxjs/operators';

export const unwrapInterceptor: HttpInterceptorFn = (req, next) => {
    return next(req).pipe(
        // Unwrap success responses
        map(event => {
            if (event instanceof HttpResponse && event.body &&
                typeof event.body === 'object' && 'data' in event.body) {
                return event.clone({ body: event.body.data });
            }
            return event;
        }),

        // Unwrap error responses (optional, but cleaner)
        catchError((error: HttpErrorResponse) => {
            if (error.error?.problemDetail) {
                return throwError(() => new HttpErrorResponse({
                    error: error.error.problemDetail,  // ⬅️ Flatten it
                    headers: error.headers,
                    status: error.status,
                    statusText: error.statusText,
                    url: error.url || undefined
                }));
            }
            return throwError(() => error);
        })
    );
};