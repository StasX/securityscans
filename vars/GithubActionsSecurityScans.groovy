def semgrepScan() {
    try {
        sh '''
        .venv/bin/semgrep scan \
            --config=p/github-actions \
            --metrics=off \
            --error \
            | tee semgrep.txt
        '''
    } finally {
        archiveArtifacts(
            artifacts:'semgrep.txt',
            fingerprint:true,
            allowEmptyArchive:true
        )
    }
}
