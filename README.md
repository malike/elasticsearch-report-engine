[![Build Status](https://travis-ci.org/malike/elasticsearch-report-engine.svg?branch=master)](https://travis-ci.org/malike/elasticsearch-report-engine) [![Coverage Status](https://coveralls.io/repos/github/malike/elasticsearch-report-engine/badge.svg?branch=master)](https://coveralls.io/github/malike/elasticsearch-report-engine?branch=master)



Plugin to generate Reports from ElasticSearch Queries.

  - [Basic Overview](#overview)
  - [Install](#install)
  - [Usage](#usage)
    - [PDF](#pdf_report)
    - [HTML](#html_report)
    - [CSV](#csv_report)
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

## PDF Report

     1. PDF Report

The plugin uses [Jasper Report](https://community.jaspersoft.com/) as core engine for generating PDF reports.
PDF templates can be designed using [iReport Designer](https://community.jaspersoft.com/wiki/ireport-designer-getting-started). This
generates a _jrmxl_ file.

The plugin generates [base64 encoded]() stream of the PDF report generated once
you pass the location of the jrxml and the query to fetch data from ElasticSearch.


 `` PDF Sample Request ``

    curl -H "Content-Type:applicaition/json" -XPOST "http://localhost:9201/_generate"  -d '{}'

  `` Parameters `` <br/>

  i. <br/>
  ii. <br/>
  iii. <br/>


 `` PDF Sample Response ``


## HTML Report

     1. HTML Report

Just like the PDF report,the HTML also uses [Jasper Report](https://community.jaspersoft.com/) as core engine for generating reports.

HTML Reports provides an alternative for use cases where reports should not be sent as an attached file.

The generates [base64 encoded]() stream of the HTML report generated. There's also an option to return the HTML string instead of the base64 encoded string.

 `` HTML Sample Request ``

    curl -H "Content-Type:applicaition/json" -XPOST "http://localhost:9201/_generate"  -d '{}'

  `` Parameters `` <br/>
  i. <br/>
  ii. <br/>
  iii. <br/>

 `` HTML Sample Response ``


 ## CSV Report

      1. CSV Report

Unlike the PDF and HTML reports,the CSV option doesn't use [Jasper Report](https://community.jaspersoft.com/) as core engine for generating reports.
Generating a CSV report uses the query and returns a [base64 encoded]() of the file.


  `` CSV Sample Request ``

    curl -H "Content-Type:applicaition/json" -XPOST "http://localhost:9201/_generate"  -d '{}'

  `` Parameters ``<br/>
    i. <br/>
    ii. <br/>
    iii. <br/>


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


