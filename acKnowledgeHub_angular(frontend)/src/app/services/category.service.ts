import { HttpClient, HttpHeaders, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Category } from '../models/category';
import { catchError, Observable, of } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class CategoryService {
  private baseUrl ='http://localhost:8080/api/v1/category';

  constructor(private http: HttpClient) { }

  add(category: Category): Observable<Category> {
    const params = new HttpParams()
        .set('name', category.name)
        .set('description', category.description)
    return this.http.post<Category>(`${this.baseUrl}/save`, null, { params })
      .pipe(
        catchError(this.handlerError<Category>('Save Category'))
      );
  }
  update(id: number, category: Category): Observable<Category> {
    const params = new HttpParams()
        .set('name', category.name)
        .set('description', category.description)

    return this.http.put<Category>(`${this.baseUrl}/update/${id}`, {withCredentials:true}, { params });
}


  getById(id: number): Observable<Category> {
    return this.http.get<Category>(`${this.baseUrl}/category/${id}`,{withCredentials:true});
  }

  getAll(): Observable<Category[]> {
    return this.http.get<Category[]>(`${this.baseUrl}/allcategories`, { withCredentials: true });
  }
    softDelete(id: number): Observable<void> {
      return this.http.put<void>(`${this.baseUrl}/softDeleteCategory/${id}`, {});
    }


    private handlerError<Category>(operation ='operation' ,result?: Category){
    return (error: any) :Observable<Category> =>{
    console.error(error) ;
    return of(result as Category);
    };
  }

}
