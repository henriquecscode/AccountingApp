import { ApplicationConfig, provideBrowserGlobalErrorListeners } from '@angular/core';
import { provideRouter } from '@angular/router';

import { routes } from './app.routes';
import { apiInterceptor } from './interceptors/api.interceptor';
import { authInterceptor } from './interceptors/auth.interceptor';
import { provideHttpClient, withInterceptors } from '@angular/common/http';
import { unwrapInterceptor } from './interceptors/unwrap.interceptor';

export const appConfig: ApplicationConfig = {
  providers: [
    provideHttpClient(
      withInterceptors(
        [
          apiInterceptor, // Redirect to backend
          unwrapInterceptor, // Unwrap response objects (data and problem detail)
          authInterceptor // Handle all authentication
        ]
      )
    ),
    provideBrowserGlobalErrorListeners(),
    provideRouter(routes)
  ]
};
