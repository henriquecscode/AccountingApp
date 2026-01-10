import { HttpInterceptorFn, HttpErrorResponse, HttpRequest } from '@angular/common/http';
import { inject } from '@angular/core';
import { AuthService } from '../services/auth.service';
import { catchError, switchMap, throwError } from 'rxjs';

export const authInterceptor: HttpInterceptorFn = (req, next) => {
  const authService = inject(AuthService);

  // Don't add token to auth endpoints
  if (isAuthenticationRequest(req)) {
    const clonedReq = req.clone({
      withCredentials: true // Allow setting and sending of cookies
    })
    return next(clonedReq);
  }

  const token = authService.getAccessToken();

  // Add access token to request, and ensure credentials are included for cookies
  const clonedReq = req.clone({
    setHeaders: token ? { Authorization: `Bearer ${token}` } : {},
  });

  return next(clonedReq).pipe(
    catchError((error: HttpErrorResponse) => {
      if (error.status === 401) {

        if (isRefreshRequest(req)) {
          // 401 for a refresh request means that refresh attempt was rejected.
          authService.logoutFrontend();
          return throwError(() => error);
        }


        // If 401, try to refresh using the cookie
        return authService.refreshAccessToken().pipe(
          switchMap(() => {
            // Retry original request with new token
            const newToken = authService.getAccessToken();
            const retryReq = req.clone({
              setHeaders: { Authorization: `Bearer ${newToken}` }
            });
            return next(retryReq);
          }),
          catchError(refreshError => {
            return throwError(() => refreshError);
          })
        );
      }

      return throwError(() => error);
    })
  );
};

function isAuthenticationRequest(req: HttpRequest<any>) {
  return isRefreshRequest(req) || isLogInRequest(req) || isSignUpRequest(req);
}

function isRefreshRequest(req: HttpRequest<any>) {
  return req.url.includes('/auth/refresh')
}

function isLogInRequest(req: HttpRequest<any>) {
  return req.url.includes('/auth/login')
}

function isSignUpRequest(req: HttpRequest<any>) {
  return req.url.includes('/auth/signup')
}

function isLogoutRequest(req: HttpRequest<any>) {
  return req.url.includes('/auth/logout')
}


