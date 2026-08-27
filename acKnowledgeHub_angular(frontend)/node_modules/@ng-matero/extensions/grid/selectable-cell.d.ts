import { EventEmitter } from '@angular/core';
import * as i0 from "@angular/core";
export declare class MtxGridSelectableCell {
    ctrlKeyPressed: boolean;
    shiftKeyPressed: boolean;
    get selected(): boolean;
    private _selected;
    cellSelectable: boolean;
    cellSelectedChange: EventEmitter<MtxGridSelectableCell>;
    onClick(event: MouseEvent): void;
    select(): void;
    deselect(): void;
    toggle(): void;
    static ɵfac: i0.ɵɵFactoryDeclaration<MtxGridSelectableCell, never>;
    static ɵdir: i0.ɵɵDirectiveDeclaration<MtxGridSelectableCell, "[mtx-grid-selectable-cell]", never, { "cellSelectable": { "alias": "cellSelectable"; "required": false; }; }, { "cellSelectedChange": "cellSelectedChange"; }, never, never, true, never>;
}
