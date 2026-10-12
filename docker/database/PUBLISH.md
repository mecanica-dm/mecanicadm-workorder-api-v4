# Publish Database Seeder

## Build

```bash
docker build -t mecanicadm-workorder-seeder -f Dockerfile.seeder .
```

## Run locally

```bash
docker run --rm --env-file .env mecanicadm-workorder-seeder
```

## Publish to a registry

Replace `<registry>` with your registry URL (e.g., `docker.io`, `ghcr.io`).

```bash
docker tag mecanicadm-workorder-seeder diegopriess/mecanicadm-workorder-seeder:latest
docker push diegopriess/mecanicadm-workorder-seeder:latest
```
