import {Injectable} from '@angular/core';
import {User} from '../model/User';
import {HttpClient, HttpEvent, HttpResponse} from '@angular/common/http';
import {environment} from '../../environments/environment';
import {catchError, map, Observable, of, tap} from 'rxjs';
import {AppConstants} from '../constants/app-constants';

@Injectable({
  providedIn: 'root',
})
export class AuthService {
  public currentUser: User | null;
  public accessToken: string;

  constructor(private http: HttpClient) {
  }

  public login(user: User): Observable<any> {
    return this.http
      .post<User>(
        environment.rootURL + AppConstants.API_ACCOUNT_LOGIN_PATH,
        user,
        {
          observe: 'response',
          withCredentials: true,
          headers: {
            Authorization:
              'Basic ' + window.btoa(user.email + ':' + user.password),
          },
        }
      )
      .pipe(
        tap((res) => {
          this.setSession(res);
        })
      );
  }

  public isAuthenticated(): Observable<boolean> {
    if (this.parseUser(this.accessToken)) {
      return of(true);
    }

    return this.refreshToken().pipe(map(res => {
      return this.setSession(res)
    })).pipe(catchError(err => {
      return of(false);
    }));
  }


  public logout(): void {

  }

  public setSession(authResult: HttpResponse<any>): boolean {
    console.log('refreshing token..');
    const tokenHeader = authResult.headers.get('X-Access-Token');

    if (!tokenHeader) return false;
    this.accessToken = tokenHeader;
    const jwt = this.extractJWTBody(tokenHeader);
    if (!jwt) return false;

    const parsedUser = this.parseUser(jwt);
    if (parsedUser) {
      this.currentUser = parsedUser;
    }
    return true;
  }

  private parseUser(token: string): User | null {
    const jwtBody = this.extractJWTBody(token);
    if (!jwtBody) return null;
    const {sub, exp} = jwtBody;
    const isExpired = this.isExpired(new Date(exp * 1000));
    if (isExpired) {
      return null;
    }
    const user = new User();
    user.email = sub;
    return user;
  }

  private extractJWTBody(token: string): any | null {
    try {
      return JSON.parse(window.atob(token.split('.')[1]));
    } catch (e) {
      return null;
    }
  }

  public refreshToken(): Observable<HttpResponse<unknown>> {
    return this.http.get<HttpEvent<unknown>>(
      environment.rootURL + AppConstants.API_REFRESH_TOKEN,
      {
        observe: 'response',
        withCredentials: true,
      }
    );
  }

  private isExpired(expirationDate: Date): boolean {
    return new Date() >= expirationDate;
  }
}
