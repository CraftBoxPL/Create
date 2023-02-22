package com.simibubi.create.content.contraptions.fluids.pipes;

import com.jozufozu.flywheel.api.MaterialManager;
import com.jozufozu.flywheel.api.instance.DynamicInstance;
import com.jozufozu.flywheel.core.Materials;
import com.jozufozu.flywheel.core.materials.model.ModelData;
import com.simibubi.create.AllBlockPartials;
import com.simibubi.create.content.contraptions.base.KineticBlockEntityRenderer;
import com.simibubi.create.content.contraptions.relays.encased.ShaftInstance;
import com.simibubi.create.foundation.utility.AngleHelper;
import com.simibubi.create.foundation.utility.AnimationTickHolder;

import net.minecraft.core.Direction;
import net.minecraft.util.Mth;

public class FluidValveInstance extends ShaftInstance<FluidValveBlockEntity> implements DynamicInstance {

	protected ModelData pointer;

    protected final double xRot;
    protected final double yRot;
    protected final int pointerRotationOffset;

    public FluidValveInstance(MaterialManager dispatcher, FluidValveBlockEntity blockEntity) {
        super(dispatcher, blockEntity);

        Direction facing = blockState.getValue(FluidValveBlock.FACING);

        yRot = AngleHelper.horizontalAngle(facing);
        xRot = facing == Direction.UP ? 0 : facing == Direction.DOWN ? 180 : 90;

        Direction.Axis pipeAxis = FluidValveBlock.getPipeAxis(blockState);
        Direction.Axis shaftAxis = KineticBlockEntityRenderer.getRotationAxisOf(blockEntity);

        boolean twist = pipeAxis.isHorizontal() && shaftAxis == Direction.Axis.X || pipeAxis.isVertical();
        pointerRotationOffset = twist ? 90 : 0;

        pointer = materialManager.defaultSolid()
                .material(Materials.TRANSFORMED)
                .getModel(AllBlockPartials.FLUID_VALVE_POINTER, blockState).createInstance();

		transformPointer();
    }

    @Override
    public void beginFrame() {
		if (blockEntity.pointer.settled()) return;

        transformPointer();
    }

    private void transformPointer() {
        float pointerRotation = Mth.lerp(blockEntity.pointer.getValue(AnimationTickHolder.getPartialTicks()), 0, -90);

        pointer.loadIdentity()
				 .translate(getInstancePosition())
				 .centre()
				 .rotateY(yRot)
				 .rotateX(xRot)
				 .rotateY(pointerRotationOffset + pointerRotation)
				 .unCentre();
	}

    @Override
    public void updateLight() {
        super.updateLight();
        relight(pos, pointer);
    }

    @Override
    public void remove() {
        super.remove();
        pointer.delete();
    }
}
