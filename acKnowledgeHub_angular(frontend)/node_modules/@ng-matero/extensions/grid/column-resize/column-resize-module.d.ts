import * as i0 from "@angular/core";
import * as i1 from "./overlay-handle";
import * as i2 from "@angular/material/core";
import * as i3 from "@angular/cdk/overlay";
import * as i4 from "./column-resize-directives/column-resize";
import * as i5 from "./column-resize-directives/column-resize-flex";
import * as i6 from "./resizable-directives/resizable";
export declare class MatColumnResizeCommonModule {
    static ɵfac: i0.ɵɵFactoryDeclaration<MatColumnResizeCommonModule, never>;
    static ɵmod: i0.ɵɵNgModuleDeclaration<MatColumnResizeCommonModule, never, [typeof i1.MatColumnResizeOverlayHandle], [typeof i1.MatColumnResizeOverlayHandle]>;
    static ɵinj: i0.ɵɵInjectorDeclaration<MatColumnResizeCommonModule>;
}
export declare class MatColumnResizeModule {
    static ɵfac: i0.ɵɵFactoryDeclaration<MatColumnResizeModule, never>;
    static ɵmod: i0.ɵɵNgModuleDeclaration<MatColumnResizeModule, never, [typeof i2.MatCommonModule, typeof i3.OverlayModule, typeof MatColumnResizeCommonModule, typeof i4.MatColumnResize, typeof i5.MatColumnResizeFlex, typeof i6.MatResizable], [typeof i4.MatColumnResize, typeof i5.MatColumnResizeFlex, typeof i6.MatResizable]>;
    static ɵinj: i0.ɵɵInjectorDeclaration<MatColumnResizeModule>;
}
