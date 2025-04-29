package app.revanced.patches.duolingo.unlocksuper.fingerprints

import app.revanced.patcher.extensions.or
import app.revanced.patcher.fingerprint
import com.android.tools.smali.dexlib2.AccessFlags
import com.android.tools.smali.dexlib2.Opcode

internal val IsUserSuperMethodFingerprint = fingerprint {
    accessFlags(AccessFlags.PUBLIC or AccessFlags.FINAL)
    returnType("Ljava/lang/Object;")
    parameters(
        "Ljava/lang/Object;",
        "Ljava/lang/Object;"
    )
    strings("user", "heartsState", "superData")
    opcodes(Opcode.IGET_BOOLEAN)
}
