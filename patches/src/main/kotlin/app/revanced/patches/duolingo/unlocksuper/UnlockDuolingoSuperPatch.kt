package app.revanced.patches.duolingo.unlocksuper

import app.revanced.patcher.extensions.InstructionExtensions.addInstructions
import app.revanced.patcher.extensions.InstructionExtensions.getInstruction
import app.revanced.patcher.patch.bytecodePatch
import app.revanced.patcher.extensions.BytecodeContextExtensions.findResult
import com.android.tools.smali.dexlib2.Opcode
import com.android.tools.smali.dexlib2.builder.instruction.BuilderInstruction22c
import com.android.tools.smali.dexlib2.iface.instruction.TwoRegisterInstruction
import app.revanced.patches.duolingo.unlocksuper.fingerprints.IsUserSuperMethodFingerprint
import app.revanced.patches.duolingo.unlocksuper.fingerprints.UserSerializationMethodFingerprint
import app.revanced.patcher.patch.PatchException

@Suppress("unused")
val unlockDuolingoSuperPatch = bytecodePatch(
    name = "Unlock Duolingo Super",
    use = false,
) {
    compatibleWith("com.duolingo"("5.158.4")) // update version if needed

    execute {
        val isUserSuperMethod = findResult(IsUserSuperMethodFingerprint)
        val userSerializationMethod = findResult(UserSerializationMethodFingerprint)

        val isUserSuperReference = isUserSuperMethod.mutableMethod.instructions
            .filterIsInstance<BuilderInstruction22c>()
            .firstOrNull { it.opcode == Opcode.IGET_BOOLEAN }
            ?.reference
            ?: throw IsUserSuperMethodFingerprint.exception

        val assignIndex = userSerializationMethod.mutableMethod.instructions.indexOfFirst { 
            it is BuilderInstruction22c && it.opcode == Opcode.IPUT_BOOLEAN && it.reference == isUserSuperReference
        }.takeIf { it != -1 } ?: throw PatchException("Could not find assignment of isUserSuper.")

        val assignInstruction = userSerializationMethod.mutableMethod.getInstruction<TwoRegisterInstruction>(assignIndex)

        userSerializationMethod.mutableMethod.addInstructions(
            assignIndex + 1,
            """
                const/4 v${assignInstruction.registerA}, 0x1
                iput-boolean v${assignInstruction.registerA}, v0, $isUserSuperReference
            """.trimIndent()
        )
    }
}
