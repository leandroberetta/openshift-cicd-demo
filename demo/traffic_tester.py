#! /usr/bin/env python

import argparse
import requests
import time

#
# Author: Leandro Beretta <lberetta@redhat.com>
#
# Script to test traffic routing with A/B deployments in OpenShift
#


def create_arg_parser():
    """ Creates the argument parser to user. """

    custom_parser = argparse.ArgumentParser(description='Test traffic routing with A/B deployments in OpenShift')

    custom_parser.add_argument('url',
                               metavar='url',
                               type=str,
                               help='The URL to test the service (GET Method used)')

    custom_parser.add_argument('N',
                               metavar='n',
                               type=int,
                               help='The number of requests to execute')

    return custom_parser


if __name__ == '__main__':
    parser = create_arg_parser()
    args = parser.parse_args()

    try:
        for n in range(1, args.N + 1):
            response = requests.get(args.url)
            body = response.json()

            print('Request: {:2} - Status Code: {} - Version: {}'.format(n, response.status_code, body['version']))

            time.sleep(1)
    except KeyboardInterrupt:
        quit(0)