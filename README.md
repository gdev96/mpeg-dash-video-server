# Video Server

Sistema di gestione video.


## Introduzione

Il progetto si sviluppa in due parti: 
- Nella prima, che prevede il deployment su Docker, la comunicazione tra i componenti e' sincrona e avviene tramite HTTP REST. 
- Nella seconda, che prevede invece il deployment su Kubernetes prima e su OpenShift poi, la comunicazione e' resa asincrona grazie all'utilizzo di una coda Kafka.


## Parte 1

### Docker

<img width="600" src="https://github.com/gdev96/video-server/blob/master/resources/docker.svg">

Il sistema e' composto da 5 componenti:
- Un API Gateway, realizzato in NGINX, che riceve richieste dal client e le serve o reindirizza al Video Management Service;
- Due servizi, Video Management e Video Processing, realizzati rispettivamente in Spring e in Flask, che costituiscono il back-end dell'applicazione e si occupano della gestione e del processamento dei video;
- Due database MySQL, App DB e Log DB, che si occupano uno della memorizzazione dei dati dell'applicazione e l'altro dei log e delle metriche del sistema.

Il sistema e' raggiungibile sulla porta 8080 dell'host, mappata sulla porta 80 dell'API Gateway. Quest'ultimo inoltra le richieste su */vms* verso il componente Video Management Service, e quelle su */videofiles* sulla sua rootdir */var/videofiles*, che e' mappata su un volume condiviso con il Video Management e il Video Processing Service.

Il Video Management Service fornisce un'interfaccia REST per la gestione dei video e
il Video Processing Service ne effettua la codifica MPEG-DASH.

Entrambi i servizi memorizzano delle statistiche sulle richieste HTTP ricevute in un apposito database, che è distinto da quello dell'applicazione, per incrementare la fault tolerance e l'availability del sistema. Per farlo il Video Management Service fa uso di un apposito filtro da cui passano tutte le richieste che giungono all'applicazione mentre il Video Processing Service usa delle apposite funzioni che vengono richiamate prima e dopo la gestione di ogni richiesta.

E' stato scelto di utilizzare dei named volume sia per i dati dei database sia per lo storage in modo da renderli indipendenti dall'host dove il sistema e' eseguito ed evitare quindi di doverli caricare nel repository.

Per l'ambiente di developement e' stato scelto di utilizzare degli appositi volumi contententi il codice sorgente in modo tale da poter utilizzare il Live Reload.


## Parte 2

### Kubernetes

<img src="https://github.com/gdev96/video-server/blob/master/resources/kubernetes.svg">

Viene effettuato il porting dell'applicazione su Kubernetes con conseguenti modifiche strutturali rispetto a quanto fatto in Docker.
In particolare:
- L'ingress diventa il nuovo API Gateway che inoltra le richieste su */vms* verso il Video Management Service, mentre tutte le altre richieste vengono reindirizzate su NGINX. Quest'ultimo ha il compito di rendergli disponibile lo storage;
- La comunicazione tra Video Management Service e Video Processing Service non e' più diretta ma mediata da Kafka; 
- Sono stati aggiunti due componenti, lo Spout e Spark: il primo pusha i log su Kafka, mentre il secondo li recupera e li elabora;
- Sia nel Processing che nello Spout sono definiti due initContainer (client Kafka), che verificano la disponibilità del broker Kafka;
- Le variabili di configurazione dei container sono state spostate in configMap e secrets;
- I deployment comunicano mediante servizi esclusivamente di tipo clusterIP dato che la comunicazione e' solo interna.

### OpenShift

<img src="https://github.com/gdev96/video-server/blob/master/resources/openshift.svg">

Anche il deployment su OpenShift del progetto Kubernetes prevede delle modifiche al sistema. In particolare:
- L'ingress viene sostituito da un route affiancato da un deployment NGINX;
- I deployment dei database sono sostituiti dai deploymentConfig offerti da OpenShift;
- I volumi utilizzati precedentemente diventano dei persistentVolume, richiesti dai deployment attraverso dei persistentVolumeClaim;
- Per motivi di sicurezza sono stati aggiunti dei securityContext.
