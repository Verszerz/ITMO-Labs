import zipfile, os, sys

out_dir = sys.argv[1]
jar_path = sys.argv[2]

manifest = "Manifest-Version: 1.0\nMain-Class: org.example.Main\n\n"

os.makedirs(os.path.dirname(jar_path), exist_ok=True)

with zipfile.ZipFile(jar_path, "w", zipfile.ZIP_DEFLATED) as z:
    z.writestr("META-INF/MANIFEST.MF", manifest)
    for dp, dn, fn in os.walk(out_dir):
        for f in fn:
            fp = os.path.join(dp, f)
            z.write(fp, os.path.relpath(fp, out_dir))

print("[Gradle] JAR создан:", jar_path)
