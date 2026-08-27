import { PipeTransform, TemplateRef } from '@angular/core';
import * as i0 from "@angular/core";
export declare class MtxIsTemplateRefPipe implements PipeTransform {
    transform(obj: any): obj is TemplateRef<any>;
    static ɵfac: i0.ɵɵFactoryDeclaration<MtxIsTemplateRefPipe, never>;
    static ɵpipe: i0.ɵɵPipeDeclaration<MtxIsTemplateRefPipe, "isTemplateRef", true>;
}
