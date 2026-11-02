def semgrepScan() {
    try {
        sh '''
        #!/usr/bin/env bash
        set -o pipefail

        "$VENV/bin/semgrep" scan \
            --config=p/php \
            --metrics=on \
            --error \
            2>&1 | tee semgrep-report.txt
        '''
    } finally {
        archiveArtifacts(
            artifacts:'semgrep-report.txt',
            allowEmptyArchive:true,
            fingerprint:true
        )
    }
}

def composerAudit() {
    try {
        sh '''
        #!/usr/bin/env bash
        set -o pipefail

        composer audit \
            --no-interaction \
            --format=plain \
            2>&1 | tee composer-audit.txt
        '''
    } finally {
        archiveArtifacts(
            artifacts:'composer-audit.txt',
            allowEmptyArchive:true,
            fingerprint:true
        )
    }
}
