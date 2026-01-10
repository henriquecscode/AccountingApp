import { HttpInterceptorFn } from '@angular/common/http';
import { environment } from '../../environments/environment';

export const apiInterceptor: HttpInterceptorFn = (req, next) => {  
  // Only add base URL if the request doesn't already have a full URL
  if (!req.url.startsWith('http')) {
    const apiReq = req.clone({ url: `${environment.apiUrl}${req.url}` });
    console.log('🌐 API Interceptor - Modified URL:', apiReq.url);
    return next(apiReq);
  }
  
  return next(req);
};