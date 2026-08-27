import * as i0 from "@angular/core";
export type MtxProgressType = 'default' | 'info' | 'success' | 'warning' | 'danger';
export declare class MtxProgress {
    /** The progress's type. Can be `default`, `info`, `success`, `warning` or `danger`. */
    type: MtxProgressType;
    /** The value of the progress. */
    value: number;
    /** The height of the progress. */
    height: string;
    /** The text color of the progress. */
    color: string;
    /** The bar color of the progress. */
    foreground: string;
    /** The track color of the progress. */
    background: string;
    /** Whether to apply the striped class. */
    striped: boolean;
    /** Whether to apply the animated class. */
    animate: boolean;
    static ɵfac: i0.ɵɵFactoryDeclaration<MtxProgress, never>;
    static ɵcmp: i0.ɵɵComponentDeclaration<MtxProgress, "mtx-progress", ["mtxProgress"], { "type": { "alias": "type"; "required": false; }; "value": { "alias": "value"; "required": false; }; "height": { "alias": "height"; "required": false; }; "color": { "alias": "color"; "required": false; }; "foreground": { "alias": "foreground"; "required": false; }; "background": { "alias": "background"; "required": false; }; "striped": { "alias": "striped"; "required": false; }; "animate": { "alias": "animate"; "required": false; }; }, {}, never, ["*"], true, never>;
    static ngAcceptInputType_striped: unknown;
    static ngAcceptInputType_animate: unknown;
}
