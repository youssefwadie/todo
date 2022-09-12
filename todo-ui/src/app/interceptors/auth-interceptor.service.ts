import {Injectable} from '@angular/core';
import {HttpErrorResponse, HttpEvent, HttpHandler, HttpInterceptor, HttpRequest} from '@angular/common/http';
import {catchError, Observable, switchMap, throwError} from 'rxjs';
import {AuthService} from "../services/auth.service";
import {environment} from "../../environments/environment";
import {AppConstants} from "../constants/app-constants";

@Injectable()
export class AuthInterceptor implements HttpInterceptor {
  private isRefreshing = false;

  constructor(private authService: AuthService) {
  }


  intercept(request: HttpRequest<unknown>, next: HttpHandler): Observable<HttpEvent<unknown>> {
    if (request.url === (environment.rootURL + AppConstants.API_ACCOUNT_LOGIN_PATH)
      || request.url === (environment.rootURL + AppConstants.API_REFRESH_TOKEN)
      || request.url == (environment.rootURL + AppConstants.API_REGISTRATION)) {
      return next.handle(request);
    }


    if (!this.authService.accessToken) {
      return this.refreshToken(request, next);
    }

    return next.handle(this.requestWithAuthHeader(request)).pipe(catchError((err: any) => {
      if (err instanceof HttpErrorResponse && err.status === 401) {
        return this.refreshToken(request, next);
      }
      return throwError(err);
    }));
  }

  private requestWithAuthHeader(request: HttpRequest<unknown>): HttpRequest<unknown> {
    const accessToken = this.authService.accessToken;
    return request.clone({
      headers: request.headers.set('Authorization', 'Bearer ' + accessToken)
    });

  }

  private refreshToken(request: HttpRequest<any>, next: HttpHandler) {
    if (!this.isRefreshing) {
      this.isRefreshing = true;
      return this.authService.refreshToken()
        .pipe(switchMap(authResult => {
          this.isRefreshing = false;
          const sessionSet = this.authService.setSession(authResult);
          if (sessionSet) {
            return next.handle(this.requestWithAuthHeader(request));
          }
          return throwError(() => next);
        }), catchError(err => {
          this.isRefreshing = false;
          return throwError(err);
        }));
    }
    return next.handle(this.requestWithAuthHeader(request));
  }
}
