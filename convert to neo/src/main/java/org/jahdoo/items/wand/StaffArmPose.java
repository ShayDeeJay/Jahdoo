package org.jahdoo.items.wand;

//public class StaffArmPose {
//    @OnlyIn(Dist.CLIENT)
//    public static HumanoidModel.ArmPose STAFF_ARM_POS = HumanoidModel.ArmPose.create(
//        "wand_arm",
//        true,
//        (model, entity, arm) -> (arm == HumanoidArm.RIGHT ? model.rightArm : model.leftArm).xRot = Mth.lerp(.85f, (arm == HumanoidArm.RIGHT ? model.rightArm : model.leftArm).xRot, ((-(float) Math.PI / 2F) + model.head.xRot /*/ 2f*/))
//    );
//
//    public static void initializeClientHelper(Consumer<IClientItemExtensions> consumer) {
//        consumer.accept(new IClientItemExtensions() {
//            @Override
//            public HumanoidModel.@Nullable ArmPose getArmPose(LivingEntity entityLiving, InteractionHand hand, ItemStack itemStack) {
//                return STAFF_ARM_POS;
//            }
//        });
//    }
//
//
//}
