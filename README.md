[![Build Status](https://travis-ci.org/malike/elasticsearch-report-engine.svg?branch=master)](https://travis-ci.org/malike/elasticsearch-report-engine) [![Coverage Status](https://coveralls.io/repos/github/malike/elasticsearch-report-engine/badge.svg?branch=master)](https://coveralls.io/github/malike/elasticsearch-report-engine?branch=master)



Plugin to generate Reports from ElasticSearch Queries.

  - [Basic Overview](#overview)
  - [Install](#install)
  - [Usage](#usage)
    - [PDF](##pdf)
    - [HTML](##html)
    - [CSV](##csv)
 - [Supported](#supported)   
 - [Download](#download)   
 - [Contribute](CONTRIBUTING.md)
 - [Code of Conduct](CODE_OF_CONDUCT.md)
 - [License](https://github.com/malike/elasticsearch-report-engine/blob/master/LICENSE)



## Overview
Once this plugin is installed into elasticsearch search,it exposes the url http://{ip}:9200/_generate, you can run queries on your cluster with the right parameters it would return PDF,HTML or CSV file. 

<br>

## Install
``sudo bin/elasticsearch-plugin install [plugin_name] ``

<br>

## Usage

#### PDF Report

     1. PDF Report




 `` PDF Sample Request ``


 `` PDF Sample Response ``


#### HTML Report

     1. HTML Report



 `` HTML Sample Request ``


 `` HTML Sample Response ``


 #### PDF Report

      1. CSV Report



  `` CSV Sample Request ``


  `` CSV Sample Response ``



<p>&nbsp;</p>
<p>&nbsp;</p>

## Supported

ElasticSearch versions supported by this plugin include :

| ElasticSearch Version | Comments |
| --------------------- | -------- |
| [5.5]()               | Tested   |
| [5.0]()               | Tested   |
| [2.4]()               | Tested   |

<p>&nbsp;</p>
<p>&nbsp;</p>

## Download

| ElasticSearch Version | Comments |
| --------------------- | -------- |
| [5.0]()               | [zip]()  |
| [5.0]()               | [zip]()  |
| [2.4]()               | [zip]()  |

<p>&nbsp;</p>
<p>&nbsp;</p>


## Contribute

Contributions are always welcome!
Please read the [contribution guidelines](CONTRIBUTING.md) first.

## Code of Conduct

Please read [this](CODE_OF_CONDUCT.md).

## License

[GNU General Public License v3.0](https://github.com/malike/elasticsearch-report-engine/blob/master/LICENSE)


